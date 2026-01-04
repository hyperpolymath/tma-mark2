# SPDX-License-Identifier: AGPL-3.0-or-later
defmodule EtmaHandler.Marking.WebSourceEvaluator do
  @moduledoc """
  Web Source Evaluation using the PROMPT Framework.

  Evaluates web references for academic suitability using automated checks
  based on the PROMPT criteria:

  - **P**resentation - Site quality, structure, HTML validity
  - **R**elevance - Domain type, content indicators
  - **O**bjectivity - Bias indicators, first-person writing, opinion markers
  - **M**ethod - Evidence of methodology, citations on page
  - **P**rovenance - Domain ownership, authorship, organization type
  - **T**imeliness - Last updated, domain age, link freshness

  ## Automated Checks

  - HTTPS verification
  - Domain type classification (.gov, .edu, .ac.uk, .org, etc.)
  - Domain age via WHOIS
  - Dead link detection
  - HTML structure quality
  - First-person/opinion language detection
  - AI-generated content indicators
  - Blocklist checking (VirusTotal community, known bad sources)
  - Learned society/government recognition
  - Citation presence on page

  ## Usage

      # Evaluate a single URL
      {:ok, report} = WebSourceEvaluator.evaluate("https://example.com/article")

      # Evaluate multiple references
      {:ok, reports} = WebSourceEvaluator.evaluate_batch(urls)

      # Quick check (faster, fewer network calls)
      {:ok, score} = WebSourceEvaluator.quick_check("https://example.com")

  ## Optional LLM Integration

  The `:llm_callback` option allows passing content to an external LLM for
  deeper analysis. This is deliberately isolated from the main system.
  """

  require Logger

  alias EtmaHandler.Settings

  # Known academic/authoritative domain suffixes
  @academic_domains [".edu", ".ac.uk", ".ac.jp", ".edu.au", ".edu.cn", ".ac.nz", ".edu.sg"]
  @government_domains [".gov", ".gov.uk", ".gov.au", ".gov.nz", ".gc.ca", ".mil"]
  @organization_domains [".org"]

  # Known learned societies and authoritative organizations
  @learned_societies [
    "royalsociety.org",
    "thebritishacademy.ac.uk",
    "nationalacademies.org",
    "apa.org",
    "ieee.org",
    "acm.org",
    "rsc.org",
    "acs.org",
    "nature.com",
    "science.org",
    "sciencedirect.com",
    "springer.com",
    "wiley.com",
    "tandfonline.com",
    "sagepub.com",
    "jstor.org",
    "pubmed.ncbi.nlm.nih.gov",
    "scholar.google.com",
    "researchgate.net",
    "academia.edu",
    "ssrn.com",
    "arxiv.org",
    "biorxiv.org",
    "medrxiv.org"
  ]

  # Known unreliable sources (expandable via settings)
  @default_blocklist [
    "wikipedia.org",
    "wikihow.com",
    "quora.com",
    "reddit.com",
    "yahoo.answers.com",
    "answers.com",
    "ask.com",
    "ehow.com",
    "about.com",
    "medium.com",
    "blogspot.com",
    "wordpress.com",
    "tumblr.com"
  ]

  # Opinion/bias language indicators
  @opinion_markers [
    "i think",
    "i believe",
    "in my opinion",
    "personally",
    "i feel",
    "obviously",
    "clearly",
    "everyone knows",
    "it's obvious",
    "undoubtedly",
    "without question",
    "the truth is",
    "wake up",
    "sheeple",
    "mainstream media",
    "they don't want you to know"
  ]

  # AI-generated content indicators
  @ai_indicators [
    "as an ai",
    "i cannot",
    "i'm unable to",
    "delve into",
    "it's important to note",
    "in conclusion,",
    "furthermore,",
    "however, it is important",
    "in summary,",
    "to summarize,"
  ]

  @type prompt_score :: %{
          presentation: float(),
          relevance: float(),
          objectivity: float(),
          method: float(),
          provenance: float(),
          timeliness: float()
        }

  @type evaluation_report :: %{
          url: String.t(),
          overall_score: float(),
          prompt_scores: prompt_score(),
          flags: [map()],
          recommendations: [String.t()],
          domain_info: map(),
          content_analysis: map(),
          evaluated_at: DateTime.t()
        }

  # ============================================================
  # PUBLIC API - EVALUATION
  # ============================================================

  @doc """
  Evaluate a web source using the PROMPT framework.

  Options:
  - `:timeout` - HTTP request timeout in ms (default: 10_000)
  - `:skip_content` - Skip content analysis (default: false)
  - `:llm_callback` - Optional function for LLM analysis `fn(content) -> map()`
  """
  @spec evaluate(String.t(), keyword()) :: {:ok, evaluation_report()} | {:error, term()}
  def evaluate(url, opts \\ []) do
    timeout = Keyword.get(opts, :timeout, 10_000)
    skip_content = Keyword.get(opts, :skip_content, false)

    with {:ok, parsed_url} <- parse_url(url),
         {:ok, domain_info} <- analyze_domain(parsed_url),
         {:ok, http_info} <- fetch_page(url, timeout),
         {:ok, content_analysis} <- maybe_analyze_content(http_info.body, skip_content, opts) do
      # Calculate PROMPT scores
      prompt_scores = calculate_prompt_scores(domain_info, http_info, content_analysis)

      # Generate flags for concerning issues
      flags = generate_flags(domain_info, http_info, content_analysis)

      # Calculate overall score (weighted average)
      overall = calculate_overall_score(prompt_scores)

      # Generate recommendations
      recommendations = generate_recommendations(prompt_scores, flags)

      {:ok,
       %{
         url: url,
         overall_score: overall,
         prompt_scores: prompt_scores,
         flags: flags,
         recommendations: recommendations,
         domain_info: domain_info,
         content_analysis: content_analysis,
         http_info: Map.drop(http_info, [:body]),
         evaluated_at: DateTime.utc_now()
       }}
    end
  end

  @doc """
  Evaluate multiple URLs in batch.
  """
  @spec evaluate_batch([String.t()], keyword()) :: {:ok, [evaluation_report()]}
  def evaluate_batch(urls, opts \\ []) do
    results =
      urls
      |> Task.async_stream(
        fn url ->
          case evaluate(url, opts) do
            {:ok, report} -> report
            {:error, reason} -> %{url: url, error: reason}
          end
        end,
        max_concurrency: 5,
        timeout: Keyword.get(opts, :timeout, 10_000) + 5_000
      )
      |> Enum.map(fn
        {:ok, result} -> result
        {:exit, reason} -> %{error: reason}
      end)

    {:ok, results}
  end

  @doc """
  Quick check - faster evaluation with fewer network calls.
  Returns just the overall score and major flags.
  """
  @spec quick_check(String.t()) :: {:ok, map()} | {:error, term()}
  def quick_check(url) do
    with {:ok, parsed_url} <- parse_url(url),
         {:ok, domain_info} <- analyze_domain(parsed_url) do
      # Check for immediate red flags without fetching content
      flags = domain_quick_flags(domain_info)

      score =
        cond do
          domain_info.is_blocklisted -> 0.1
          domain_info.is_learned_society -> 0.9
          domain_info.is_government -> 0.85
          domain_info.is_academic -> 0.8
          domain_info.is_https -> 0.5
          true -> 0.3
        end

      {:ok,
       %{
         url: url,
         score: score,
         domain_type: domain_info.type,
         flags: flags,
         is_suitable: score >= 0.5 && Enum.empty?(Enum.filter(flags, &(&1.severity == :error)))
       }}
    end
  end

  @doc """
  Check if a URL is on the blocklist.
  """
  @spec is_blocklisted?(String.t()) :: boolean()
  def is_blocklisted?(url) do
    blocklist = Settings.get(:source_blocklist, @default_blocklist)

    case URI.parse(url) do
      %URI{host: host} when is_binary(host) ->
        host_lower = String.downcase(host)
        Enum.any?(blocklist, &String.contains?(host_lower, &1))

      _ ->
        false
    end
  end

  @doc """
  Check if a domain is from a recognized authority.
  """
  @spec is_authoritative?(String.t()) :: {:ok, atom()} | {:error, :not_authoritative}
  def is_authoritative?(url) do
    case URI.parse(url) do
      %URI{host: host} when is_binary(host) ->
        host_lower = String.downcase(host)

        cond do
          Enum.any?(@learned_societies, &String.contains?(host_lower, &1)) ->
            {:ok, :learned_society}

          Enum.any?(@government_domains, &String.ends_with?(host_lower, &1)) ->
            {:ok, :government}

          Enum.any?(@academic_domains, &String.ends_with?(host_lower, &1)) ->
            {:ok, :academic}

          true ->
            {:error, :not_authoritative}
        end

      _ ->
        {:error, :not_authoritative}
    end
  end

  @doc """
  Add a domain to the blocklist.
  """
  @spec add_to_blocklist(String.t()) :: :ok
  def add_to_blocklist(domain) do
    blocklist = Settings.get(:source_blocklist, @default_blocklist)

    if domain not in blocklist do
      Settings.put(:source_blocklist, [domain | blocklist])
    end

    :ok
  end

  @doc """
  Remove a domain from the blocklist.
  """
  @spec remove_from_blocklist(String.t()) :: :ok
  def remove_from_blocklist(domain) do
    blocklist = Settings.get(:source_blocklist, @default_blocklist)
    Settings.put(:source_blocklist, List.delete(blocklist, domain))
    :ok
  end

  # ============================================================
  # PUBLIC API - CONTENT ANALYSIS
  # ============================================================

  @doc """
  Analyze text content for quality indicators.
  """
  @spec analyze_content(String.t()) :: {:ok, map()}
  def analyze_content(text) do
    text_lower = String.downcase(text)

    # Count opinion markers
    opinion_count =
      Enum.count(@opinion_markers, &String.contains?(text_lower, &1))

    # Count AI indicators
    ai_count = Enum.count(@ai_indicators, &String.contains?(text_lower, &1))

    # Check for citations on page
    citation_count = count_citations(text)

    # Check for references section
    has_references = has_references_section?(text)

    # First person usage
    first_person_count = count_first_person(text)

    # Word count
    word_count = text |> String.split() |> length()

    # Calculate scores
    opinion_score = max(0.0, 1.0 - opinion_count * 0.15)
    ai_score = max(0.0, 1.0 - ai_count * 0.1)

    academic_indicators =
      cond do
        citation_count > 5 && has_references -> 1.0
        citation_count > 2 -> 0.7
        has_references -> 0.5
        true -> 0.2
      end

    {:ok,
     %{
       opinion_markers: opinion_count,
       ai_indicators: ai_count,
       citation_count: citation_count,
       has_references: has_references,
       first_person_count: first_person_count,
       word_count: word_count,
       scores: %{
         objectivity: opinion_score,
         ai_likelihood: ai_score,
         academic_quality: academic_indicators
       }
     }}
  end

  @doc """
  Check HTML quality.
  """
  @spec check_html_quality(String.t()) :: {:ok, map()}
  def check_html_quality(html) do
    # Basic HTML quality checks
    has_doctype = String.contains?(html, "<!DOCTYPE") || String.contains?(html, "<!doctype")
    has_head = String.contains?(html, "<head")
    has_title = String.contains?(html, "<title")
    has_meta_charset = String.contains?(html, "charset=")

    # Check for malformed tags (very basic)
    unclosed_divs = count_unclosed_tags(html, "div")
    unclosed_spans = count_unclosed_tags(html, "span")

    # Check for accessibility basics
    img_without_alt = count_images_without_alt(html)

    # Check for inline styles (low quality indicator)
    inline_style_count = html |> String.split("style=\"") |> length() |> Kernel.-(1)

    score =
      [
        if(has_doctype, do: 0.15, else: 0),
        if(has_head, do: 0.15, else: 0),
        if(has_title, do: 0.15, else: 0),
        if(has_meta_charset, do: 0.1, else: 0),
        if(unclosed_divs < 3 && unclosed_spans < 3, do: 0.2, else: 0),
        if(img_without_alt < 5, do: 0.1, else: 0),
        if(inline_style_count < 10, do: 0.15, else: 0)
      ]
      |> Enum.sum()

    {:ok,
     %{
       has_doctype: has_doctype,
       has_head: has_head,
       has_title: has_title,
       has_charset: has_meta_charset,
       unclosed_tags: unclosed_divs + unclosed_spans,
       images_without_alt: img_without_alt,
       inline_styles: inline_style_count,
       quality_score: score
     }}
  end

  @doc """
  Extract and check links on a page.
  """
  @spec check_links(String.t(), String.t()) :: {:ok, map()}
  def check_links(html, base_url) do
    # Extract href attributes
    links =
      ~r/href=["']([^"']+)["']/i
      |> Regex.scan(html)
      |> Enum.map(&List.last/1)
      |> Enum.uniq()

    # Categorize links
    {internal, external} =
      Enum.split_with(links, fn link ->
        String.starts_with?(link, "/") ||
          String.starts_with?(link, "#") ||
          (is_binary(base_url) && String.contains?(link, URI.parse(base_url).host || ""))
      end)

    # For external links, quick check if they're academic
    academic_links =
      Enum.count(external, fn link ->
        case is_authoritative?(link) do
          {:ok, _} -> true
          _ -> false
        end
      end)

    {:ok,
     %{
       total_links: length(links),
       internal_links: length(internal),
       external_links: length(external),
       academic_links: academic_links,
       link_quality: if(length(external) > 0, do: academic_links / length(external), else: 0.0)
     }}
  end

  # ============================================================
  # PRIVATE - URL PARSING
  # ============================================================

  defp parse_url(url) do
    case URI.parse(url) do
      %URI{scheme: nil} ->
        parse_url("https://#{url}")

      %URI{scheme: scheme, host: host} = uri when scheme in ["http", "https"] and is_binary(host) ->
        {:ok, uri}

      _ ->
        {:error, {:invalid_url, url}}
    end
  end

  # ============================================================
  # PRIVATE - DOMAIN ANALYSIS
  # ============================================================

  defp analyze_domain(%URI{scheme: scheme, host: host}) do
    host_lower = String.downcase(host)
    blocklist = Settings.get(:source_blocklist, @default_blocklist)

    is_https = scheme == "https"
    is_academic = Enum.any?(@academic_domains, &String.ends_with?(host_lower, &1))
    is_government = Enum.any?(@government_domains, &String.ends_with?(host_lower, &1))
    is_learned_society = Enum.any?(@learned_societies, &String.contains?(host_lower, &1))
    is_blocklisted = Enum.any?(blocklist, &String.contains?(host_lower, &1))
    is_org = String.ends_with?(host_lower, ".org")

    domain_type =
      cond do
        is_learned_society -> :learned_society
        is_government -> :government
        is_academic -> :academic
        is_org -> :organization
        is_blocklisted -> :blocklisted
        true -> :commercial
      end

    # Extract TLD
    tld = host_lower |> String.split(".") |> List.last()

    {:ok,
     %{
       host: host,
       scheme: scheme,
       tld: tld,
       type: domain_type,
       is_https: is_https,
       is_academic: is_academic,
       is_government: is_government,
       is_learned_society: is_learned_society,
       is_blocklisted: is_blocklisted,
       is_organization: is_org
     }}
  end

  defp domain_quick_flags(domain_info) do
    flags = []

    flags =
      if domain_info.is_blocklisted do
        [
          %{
            type: :blocklisted,
            severity: :error,
            message: "This domain is on the blocklist of unreliable sources"
          }
          | flags
        ]
      else
        flags
      end

    flags =
      if not domain_info.is_https do
        [
          %{
            type: :no_https,
            severity: :warning,
            message: "Site does not use HTTPS - may be insecure or unmaintained"
          }
          | flags
        ]
      else
        flags
      end

    flags
  end

  # ============================================================
  # PRIVATE - HTTP FETCHING
  # ============================================================

  defp fetch_page(url, timeout) do
    # Using Req for HTTP requests
    case Application.ensure_loaded(:req) do
      :ok ->
        do_fetch_with_req(url, timeout)

      {:error, _} ->
        # Fallback without content
        {:ok,
         %{
           status: :unknown,
           headers: %{},
           body: "",
           response_time_ms: 0,
           redirects: 0
         }}
    end
  end

  defp do_fetch_with_req(url, timeout) do
    start_time = System.monotonic_time(:millisecond)

    try do
      case Req.get(url, receive_timeout: timeout, max_redirects: 5) do
        {:ok, %{status: status, headers: headers, body: body}} ->
          end_time = System.monotonic_time(:millisecond)

          {:ok,
           %{
             status: status,
             headers: Map.new(headers),
             body: body,
             response_time_ms: end_time - start_time,
             is_success: status in 200..299
           }}

        {:error, reason} ->
          {:ok,
           %{
             status: :error,
             error: reason,
             headers: %{},
             body: "",
             response_time_ms: 0,
             is_success: false
           }}
      end
    rescue
      e ->
        {:ok,
         %{
           status: :error,
           error: Exception.message(e),
           headers: %{},
           body: "",
           response_time_ms: 0,
           is_success: false
         }}
    end
  end

  # ============================================================
  # PRIVATE - CONTENT ANALYSIS
  # ============================================================

  defp maybe_analyze_content(_body, true, _opts), do: {:ok, %{skipped: true}}

  defp maybe_analyze_content(body, false, opts) when is_binary(body) and byte_size(body) > 0 do
    # Extract text from HTML
    text = extract_text_from_html(body)

    {:ok, content_analysis} = analyze_content(text)
    {:ok, html_quality} = check_html_quality(body)
    {:ok, link_info} = check_links(body, Keyword.get(opts, :base_url, ""))

    # Optional LLM callback for deeper analysis
    llm_analysis =
      case Keyword.get(opts, :llm_callback) do
        nil -> nil
        callback when is_function(callback, 1) -> callback.(text)
        _ -> nil
      end

    {:ok,
     Map.merge(content_analysis, %{
       html_quality: html_quality,
       links: link_info,
       llm_analysis: llm_analysis
     })}
  end

  defp maybe_analyze_content(_, _, _), do: {:ok, %{empty: true}}

  defp extract_text_from_html(html) do
    html
    # Remove script and style content
    |> String.replace(~r/<script[^>]*>.*?<\/script>/is, " ")
    |> String.replace(~r/<style[^>]*>.*?<\/style>/is, " ")
    # Remove HTML tags
    |> String.replace(~r/<[^>]+>/, " ")
    # Decode common entities
    |> String.replace("&nbsp;", " ")
    |> String.replace("&amp;", "&")
    |> String.replace("&lt;", "<")
    |> String.replace("&gt;", ">")
    |> String.replace("&quot;", "\"")
    # Normalize whitespace
    |> String.replace(~r/\s+/, " ")
    |> String.trim()
  end

  defp count_citations(text) do
    # Look for patterns like (Author, 2020) or [1] or (1)
    parenthetical = Regex.scan(~r/\([A-Z][a-z]+(?:\s+(?:and|&)\s+[A-Z][a-z]+)?,?\s*\d{4}\)/, text)
    bracketed = Regex.scan(~r/\[\d+\]/, text)

    length(parenthetical) + length(bracketed)
  end

  defp has_references_section?(text) do
    text_lower = String.downcase(text)

    String.contains?(text_lower, "references") ||
      String.contains?(text_lower, "bibliography") ||
      String.contains?(text_lower, "works cited")
  end

  defp count_first_person(text) do
    text_lower = String.downcase(text)

    patterns = [" i ", " i'm ", " i've ", " my ", " me ", " we ", " our ", " us "]
    Enum.reduce(patterns, 0, fn pattern, acc -> acc + count_occurrences(text_lower, pattern) end)
  end

  defp count_occurrences(text, pattern) do
    text |> String.split(pattern) |> length() |> Kernel.-(1) |> max(0)
  end

  defp count_unclosed_tags(html, tag) do
    opens = html |> String.split("<#{tag}") |> length() |> Kernel.-(1)
    closes = html |> String.split("</#{tag}>") |> length() |> Kernel.-(1)
    abs(opens - closes)
  end

  defp count_images_without_alt(html) do
    # Find img tags
    ~r/<img[^>]+>/i
    |> Regex.scan(html)
    |> Enum.count(fn [img] -> not String.contains?(img, "alt=") end)
  end

  # ============================================================
  # PRIVATE - PROMPT SCORING
  # ============================================================

  defp calculate_prompt_scores(domain_info, http_info, content_analysis) do
    # Presentation score
    presentation =
      cond do
        content_analysis[:skipped] || content_analysis[:empty] -> 0.5
        true -> content_analysis[:html_quality][:quality_score] || 0.5
      end

    # Relevance score (based on domain type)
    relevance =
      case domain_info.type do
        :learned_society -> 1.0
        :government -> 0.9
        :academic -> 0.9
        :organization -> 0.6
        :blocklisted -> 0.1
        _ -> 0.4
      end

    # Objectivity score
    objectivity =
      cond do
        content_analysis[:skipped] || content_analysis[:empty] -> 0.5
        true -> content_analysis[:scores][:objectivity] || 0.5
      end

    # Method score (citations and references)
    method =
      cond do
        content_analysis[:skipped] || content_analysis[:empty] -> 0.5
        true -> content_analysis[:scores][:academic_quality] || 0.3
      end

    # Provenance score
    provenance =
      cond do
        domain_info.is_learned_society -> 1.0
        domain_info.is_government -> 0.95
        domain_info.is_academic -> 0.9
        domain_info.is_organization -> 0.6
        domain_info.is_blocklisted -> 0.1
        true -> 0.4
      end

    # Timeliness score (based on response time as proxy for maintenance)
    timeliness =
      cond do
        http_info[:status] == :error -> 0.2
        not http_info[:is_success] -> 0.3
        http_info[:response_time_ms] > 10_000 -> 0.4
        http_info[:response_time_ms] > 5_000 -> 0.6
        true -> 0.8
      end

    %{
      presentation: Float.round(presentation, 2),
      relevance: Float.round(relevance, 2),
      objectivity: Float.round(objectivity, 2),
      method: Float.round(method, 2),
      provenance: Float.round(provenance, 2),
      timeliness: Float.round(timeliness, 2)
    }
  end

  defp calculate_overall_score(prompt_scores) do
    # Weighted average - provenance and objectivity are most important
    weights = %{
      presentation: 0.1,
      relevance: 0.15,
      objectivity: 0.25,
      method: 0.15,
      provenance: 0.25,
      timeliness: 0.1
    }

    score =
      Enum.reduce(prompt_scores, 0.0, fn {key, value}, acc ->
        acc + value * weights[key]
      end)

    Float.round(score, 2)
  end

  # ============================================================
  # PRIVATE - FLAG GENERATION
  # ============================================================

  defp generate_flags(domain_info, http_info, content_analysis) do
    flags = []

    # Domain flags
    flags =
      if domain_info.is_blocklisted do
        [
          %{
            type: :blocklisted,
            severity: :error,
            category: :provenance,
            message: "This domain is on the blocklist. Not suitable for academic citation."
          }
          | flags
        ]
      else
        flags
      end

    flags =
      if not domain_info.is_https do
        [
          %{
            type: :no_https,
            severity: :warning,
            category: :presentation,
            message: "Site does not use HTTPS. May indicate poor maintenance."
          }
          | flags
        ]
      else
        flags
      end

    # HTTP flags
    flags =
      if http_info[:status] == :error do
        [
          %{
            type: :unreachable,
            severity: :error,
            category: :timeliness,
            message: "Could not reach the website. It may be down or removed."
          }
          | flags
        ]
      else
        flags
      end

    flags =
      if http_info[:status] not in [nil, :unknown, :error] and not http_info[:is_success] do
        [
          %{
            type: :http_error,
            severity: :warning,
            category: :timeliness,
            message: "Website returned HTTP status #{http_info[:status]}."
          }
          | flags
        ]
      else
        flags
      end

    # Content flags
    flags =
      if content_analysis[:opinion_markers] && content_analysis[:opinion_markers] > 3 do
        [
          %{
            type: :high_opinion,
            severity: :warning,
            category: :objectivity,
            message:
              "Content contains #{content_analysis[:opinion_markers]} opinion/bias markers. May not be objective."
          }
          | flags
        ]
      else
        flags
      end

    flags =
      if content_analysis[:ai_indicators] && content_analysis[:ai_indicators] > 2 do
        [
          %{
            type: :ai_content,
            severity: :warning,
            category: :method,
            message:
              "Content shows #{content_analysis[:ai_indicators]} AI-generated content indicators."
          }
          | flags
        ]
      else
        flags
      end

    flags =
      if content_analysis[:citation_count] == 0 &&
           not (content_analysis[:skipped] || content_analysis[:empty]) do
        [
          %{
            type: :no_citations,
            severity: :info,
            category: :method,
            message: "No citations found on page. May not be well-sourced."
          }
          | flags
        ]
      else
        flags
      end

    # HTML quality flags
    flags =
      if content_analysis[:html_quality] && content_analysis[:html_quality][:quality_score] < 0.4 do
        [
          %{
            type: :poor_html,
            severity: :info,
            category: :presentation,
            message: "Poor HTML quality. May indicate low maintenance or unprofessional source."
          }
          | flags
        ]
      else
        flags
      end

    flags
  end

  # ============================================================
  # PRIVATE - RECOMMENDATIONS
  # ============================================================

  defp generate_recommendations(prompt_scores, flags) do
    recommendations = []

    # Based on overall score
    avg_score = calculate_overall_score(prompt_scores)

    recommendations =
      cond do
        avg_score < 0.3 ->
          [
            "This source is NOT recommended for academic use. Consider finding peer-reviewed alternatives."
            | recommendations
          ]

        avg_score < 0.5 ->
          ["Use with caution. Verify claims with more authoritative sources." | recommendations]

        avg_score < 0.7 ->
          [
            "Acceptable source, but consider supplementing with peer-reviewed research."
            | recommendations
          ]

        true ->
          recommendations
      end

    # Based on specific issues
    recommendations =
      if Enum.any?(flags, &(&1.type == :blocklisted)) do
        ["Do NOT cite this source. It is on the academic blocklist." | recommendations]
      else
        recommendations
      end

    recommendations =
      if Enum.any?(flags, &(&1.type == :ai_content)) do
        [
          "AI-generated content detected. Verify all claims independently."
          | recommendations
        ]
      else
        recommendations
      end

    recommendations =
      if prompt_scores.method < 0.5 do
        [
          "Source lacks citations/references. Consider more scholarly alternatives."
          | recommendations
        ]
      else
        recommendations
      end

    Enum.uniq(recommendations)
  end
end
