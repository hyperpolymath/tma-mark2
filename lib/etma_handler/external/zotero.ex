# SPDX-License-Identifier: AGPL-3.0-or-later
defmodule EtmaHandler.External.Zotero do
  @moduledoc """
  Zotero Integration for Reference Lookup.

  Connects to Zotero's local API to:

  1. **Search** - Find papers in your library
  2. **Verify** - Check if a student's citation exists
  3. **Retrieve** - Get bibliographic details
  4. **Collections** - Access organized collections

  ## Configuration

  Zotero must be running with the "Better BibTeX" or default connector enabled.
  Default local API: http://localhost:23119/api

  For web API access, configure your API key:

      Settings.put([:zotero, :api_key], "your-key")
      Settings.put([:zotero, :user_id], "12345")

  ## Usage

      # Search local Zotero library
      {:ok, results} = Zotero.search("Smith 2020 climate")

      # Check if a reference exists
      {:ok, item} = Zotero.find_by_citation("Smith, 2020")

      # Get all items in a collection
      {:ok, items} = Zotero.collection_items("Module Materials")

      # Verify student references against your library
      {:ok, verified} = Zotero.verify_references(parsed_references)
  """

  require Logger

  alias EtmaHandler.Settings

  @local_api_url "http://localhost:23119"
  @web_api_url "https://api.zotero.org"

  @type item :: %{
          key: String.t(),
          title: String.t(),
          authors: [String.t()],
          year: integer() | String.t(),
          item_type: String.t(),
          abstract: String.t() | nil,
          doi: String.t() | nil,
          url: String.t() | nil,
          publication: String.t() | nil,
          tags: [String.t()]
        }

  @type search_result :: %{
          total: integer(),
          items: [item()]
        }

  # ============================================================
  # PUBLIC API - SEARCH
  # ============================================================

  @doc """
  Search the Zotero library.

  ## Options

  - `:collection` - Limit search to a specific collection
  - `:limit` - Maximum results (default: 25)
  - `:item_type` - Filter by type (journalArticle, book, etc.)
  - `:use_web` - Force web API (default: tries local first)
  """
  @spec search(String.t(), keyword()) :: {:ok, search_result()} | {:error, term()}
  def search(query, opts \\ []) do
    if Keyword.get(opts, :use_web, false) do
      search_web(query, opts)
    else
      case search_local(query, opts) do
        {:ok, _} = result -> result
        {:error, :local_unavailable} -> search_web(query, opts)
        error -> error
      end
    end
  end

  @doc """
  Find an item by citation (Author, Year format).
  """
  @spec find_by_citation(String.t()) :: {:ok, item()} | {:error, :not_found | term()}
  def find_by_citation(citation) do
    # Parse citation like "Smith, 2020" or "Smith & Jones, 2019"
    case parse_citation(citation) do
      {:ok, author, year} ->
        search_query = "#{author} #{year}"

        case search(search_query, limit: 10) do
          {:ok, %{items: items}} ->
            # Find best match
            match =
              Enum.find(items, fn item ->
                matches_citation?(item, author, year)
              end)

            if match do
              {:ok, match}
            else
              {:error, :not_found}
            end

          error ->
            error
        end

      :error ->
        {:error, :invalid_citation}
    end
  end

  @doc """
  Get an item by its Zotero key.
  """
  @spec get_item(String.t()) :: {:ok, item()} | {:error, term()}
  def get_item(key) do
    case get_local_item(key) do
      {:ok, _} = result -> result
      {:error, :local_unavailable} -> get_web_item(key)
      error -> error
    end
  end

  # ============================================================
  # PUBLIC API - COLLECTIONS
  # ============================================================

  @doc """
  List all collections.
  """
  @spec list_collections() :: {:ok, [map()]} | {:error, term()}
  def list_collections do
    case list_local_collections() do
      {:ok, _} = result -> result
      {:error, :local_unavailable} -> list_web_collections()
      error -> error
    end
  end

  @doc """
  Get items in a collection by name or key.
  """
  @spec collection_items(String.t(), keyword()) :: {:ok, [item()]} | {:error, term()}
  def collection_items(collection_name_or_key, opts \\ []) do
    case get_collection_key(collection_name_or_key) do
      {:ok, key} ->
        fetch_collection_items(key, opts)

      {:error, :not_found} ->
        {:error, {:collection_not_found, collection_name_or_key}}
    end
  end

  @doc """
  Create a collection for module materials.
  """
  @spec create_collection(String.t(), String.t() | nil) :: {:ok, String.t()} | {:error, term()}
  def create_collection(name, parent_key \\ nil) do
    create_web_collection(name, parent_key)
  end

  # ============================================================
  # PUBLIC API - VERIFICATION
  # ============================================================

  @doc """
  Verify a list of parsed references against the Zotero library.

  Returns which references were found, not found, or partially matched.
  """
  @spec verify_references([map()]) :: {:ok, map()}
  def verify_references(references) do
    results =
      Enum.map(references, fn ref ->
        author = ref[:author] || ""
        year = ref[:year] || ""
        citation = "#{author}, #{year}"

        case find_by_citation(citation) do
          {:ok, item} ->
            %{
              reference: ref,
              status: :verified,
              zotero_item: item,
              message: "Found in library: #{item.title}"
            }

          {:error, :not_found} ->
            %{
              reference: ref,
              status: :not_found,
              zotero_item: nil,
              message: "Not found in Zotero library"
            }

          {:error, _} ->
            %{
              reference: ref,
              status: :unknown,
              zotero_item: nil,
              message: "Could not verify (library unavailable)"
            }
        end
      end)

    summary = %{
      total: length(results),
      verified: Enum.count(results, &(&1.status == :verified)),
      not_found: Enum.count(results, &(&1.status == :not_found)),
      unknown: Enum.count(results, &(&1.status == :unknown))
    }

    {:ok, %{results: results, summary: summary}}
  end

  @doc """
  Check if a specific reference is suitable (in a "suitable" collection or tagged).
  """
  @spec is_suitable?(item()) :: boolean()
  def is_suitable?(item) do
    suitable_tags = get_suitable_tags()

    item_tags = Enum.map(item.tags || [], &String.downcase/1)

    Enum.any?(suitable_tags, fn tag ->
      String.downcase(tag) in item_tags
    end)
  end

  @doc """
  Get items tagged as suitable for a module.
  """
  @spec get_suitable_items(String.t()) :: {:ok, [item()]} | {:error, term()}
  def get_suitable_items(course_code) do
    search("tag:suitable-#{course_code}", limit: 100)
  end

  # ============================================================
  # PUBLIC API - CONFIGURATION
  # ============================================================

  @doc """
  Check if Zotero is available (local or web).
  """
  @spec available?() :: boolean()
  def available? do
    local_available?() or web_configured?()
  end

  @doc """
  Check if local Zotero is running.
  """
  @spec local_available?() :: boolean()
  def local_available? do
    case check_local_connection() do
      :ok -> true
      _ -> false
    end
  end

  @doc """
  Check if web API is configured.
  """
  @spec web_configured?() :: boolean()
  def web_configured? do
    api_key = Settings.get([:zotero, :api_key])
    user_id = Settings.get([:zotero, :user_id])
    api_key != nil and user_id != nil
  end

  @doc """
  Configure web API access.
  """
  @spec configure_web_api(String.t(), String.t()) :: :ok
  def configure_web_api(api_key, user_id) do
    Settings.put([:zotero, :api_key], api_key)
    Settings.put([:zotero, :user_id], user_id)
  end

  @doc """
  Set tags that indicate a source is suitable.
  """
  @spec set_suitable_tags([String.t()]) :: :ok
  def set_suitable_tags(tags) do
    Settings.put([:zotero, :suitable_tags], tags)
  end

  # ============================================================
  # PRIVATE - LOCAL API
  # ============================================================

  defp check_local_connection do
    url = "#{@local_api_url}/connector/ping"

    case Req.get(url, receive_timeout: 2000) do
      {:ok, %{status: 200}} -> :ok
      _ -> :error
    end
  rescue
    _ -> :error
  end

  defp search_local(query, opts) do
    limit = Keyword.get(opts, :limit, 25)

    # Better BibTeX search endpoint
    url = "#{@local_api_url}/better-bibtex/json-rpc"

    body =
      Jason.encode!(%{
        jsonrpc: "2.0",
        method: "item.search",
        params: [query],
        id: 1
      })

    case Req.post(url, body: body, headers: [{"Content-Type", "application/json"}]) do
      {:ok, %{status: 200, body: response}} ->
        items =
          response["result"]
          |> Enum.take(limit)
          |> Enum.map(&parse_local_item/1)

        {:ok, %{total: length(items), items: items}}

      {:ok, %{status: _}} ->
        # Try alternative: Zotero Connector search
        search_local_connector(query, opts)

      {:error, _} ->
        {:error, :local_unavailable}
    end
  rescue
    _ -> {:error, :local_unavailable}
  end

  defp search_local_connector(query, opts) do
    # Fallback to simpler local search
    url = "#{@local_api_url}/api/users/0/items?q=#{URI.encode(query)}"

    case Req.get(url) do
      {:ok, %{status: 200, body: items}} when is_list(items) ->
        limit = Keyword.get(opts, :limit, 25)
        parsed = items |> Enum.take(limit) |> Enum.map(&parse_api_item/1)
        {:ok, %{total: length(parsed), items: parsed}}

      _ ->
        {:error, :local_unavailable}
    end
  rescue
    _ -> {:error, :local_unavailable}
  end

  defp get_local_item(key) do
    url = "#{@local_api_url}/api/users/0/items/#{key}"

    case Req.get(url) do
      {:ok, %{status: 200, body: item}} ->
        {:ok, parse_api_item(item)}

      {:ok, %{status: 404}} ->
        {:error, :not_found}

      _ ->
        {:error, :local_unavailable}
    end
  rescue
    _ -> {:error, :local_unavailable}
  end

  defp list_local_collections do
    url = "#{@local_api_url}/api/users/0/collections"

    case Req.get(url) do
      {:ok, %{status: 200, body: collections}} when is_list(collections) ->
        parsed =
          Enum.map(collections, fn c ->
            %{
              key: c["key"],
              name: get_in(c, ["data", "name"]),
              parent: get_in(c, ["data", "parentCollection"])
            }
          end)

        {:ok, parsed}

      _ ->
        {:error, :local_unavailable}
    end
  rescue
    _ -> {:error, :local_unavailable}
  end

  defp parse_local_item(item) do
    %{
      key: item["key"] || item["itemKey"],
      title: item["title"] || "",
      authors: parse_creators(item["creators"] || []),
      year: extract_year(item["date"]),
      item_type: item["itemType"] || "unknown",
      abstract: item["abstractNote"],
      doi: item["DOI"],
      url: item["url"],
      publication: item["publicationTitle"] || item["bookTitle"],
      tags: Enum.map(item["tags"] || [], & &1["tag"])
    }
  end

  # ============================================================
  # PRIVATE - WEB API
  # ============================================================

  defp search_web(query, opts) do
    unless web_configured?() do
      {:error, :web_not_configured}
    else
      user_id = Settings.get([:zotero, :user_id])
      api_key = Settings.get([:zotero, :api_key])
      limit = Keyword.get(opts, :limit, 25)

      url =
        "#{@web_api_url}/users/#{user_id}/items?q=#{URI.encode(query)}&limit=#{limit}&format=json"

      headers = [{"Zotero-API-Key", api_key}]

      case Req.get(url, headers: headers) do
        {:ok, %{status: 200, body: items}} when is_list(items) ->
          parsed = Enum.map(items, &parse_api_item/1)
          {:ok, %{total: length(parsed), items: parsed}}

        {:ok, %{status: 403}} ->
          {:error, :unauthorized}

        {:ok, %{status: status}} ->
          {:error, {:api_error, status}}

        {:error, reason} ->
          {:error, {:request_failed, reason}}
      end
    end
  rescue
    e -> {:error, {:exception, Exception.message(e)}}
  end

  defp get_web_item(key) do
    unless web_configured?() do
      {:error, :web_not_configured}
    else
      user_id = Settings.get([:zotero, :user_id])
      api_key = Settings.get([:zotero, :api_key])

      url = "#{@web_api_url}/users/#{user_id}/items/#{key}?format=json"
      headers = [{"Zotero-API-Key", api_key}]

      case Req.get(url, headers: headers) do
        {:ok, %{status: 200, body: item}} ->
          {:ok, parse_api_item(item)}

        {:ok, %{status: 404}} ->
          {:error, :not_found}

        _ ->
          {:error, :request_failed}
      end
    end
  rescue
    _ -> {:error, :request_failed}
  end

  defp list_web_collections do
    unless web_configured?() do
      {:error, :web_not_configured}
    else
      user_id = Settings.get([:zotero, :user_id])
      api_key = Settings.get([:zotero, :api_key])

      url = "#{@web_api_url}/users/#{user_id}/collections?format=json"
      headers = [{"Zotero-API-Key", api_key}]

      case Req.get(url, headers: headers) do
        {:ok, %{status: 200, body: collections}} when is_list(collections) ->
          parsed =
            Enum.map(collections, fn c ->
              %{
                key: c["key"],
                name: get_in(c, ["data", "name"]),
                parent: get_in(c, ["data", "parentCollection"])
              }
            end)

          {:ok, parsed}

        _ ->
          {:error, :request_failed}
      end
    end
  rescue
    _ -> {:error, :request_failed}
  end

  defp create_web_collection(name, parent_key) do
    unless web_configured?() do
      {:error, :web_not_configured}
    else
      user_id = Settings.get([:zotero, :user_id])
      api_key = Settings.get([:zotero, :api_key])

      url = "#{@web_api_url}/users/#{user_id}/collections"
      headers = [{"Zotero-API-Key", api_key}, {"Content-Type", "application/json"}]

      body =
        Jason.encode!([
          %{
            name: name,
            parentCollection: parent_key
          }
        ])

      case Req.post(url, body: body, headers: headers) do
        {:ok, %{status: 200, body: [%{"key" => key} | _]}} ->
          {:ok, key}

        {:ok, %{status: status}} ->
          {:error, {:api_error, status}}

        {:error, reason} ->
          {:error, {:request_failed, reason}}
      end
    end
  rescue
    e -> {:error, {:exception, Exception.message(e)}}
  end

  defp fetch_collection_items(collection_key, opts) do
    limit = Keyword.get(opts, :limit, 100)

    if local_available?() do
      url = "#{@local_api_url}/api/users/0/collections/#{collection_key}/items?limit=#{limit}"

      case Req.get(url) do
        {:ok, %{status: 200, body: items}} when is_list(items) ->
          {:ok, Enum.map(items, &parse_api_item/1)}

        _ ->
          fetch_web_collection_items(collection_key, limit)
      end
    else
      fetch_web_collection_items(collection_key, limit)
    end
  rescue
    _ -> fetch_web_collection_items(collection_key, opts[:limit] || 100)
  end

  defp fetch_web_collection_items(collection_key, limit) do
    unless web_configured?() do
      {:error, :not_configured}
    else
      user_id = Settings.get([:zotero, :user_id])
      api_key = Settings.get([:zotero, :api_key])

      url =
        "#{@web_api_url}/users/#{user_id}/collections/#{collection_key}/items?format=json&limit=#{limit}"

      headers = [{"Zotero-API-Key", api_key}]

      case Req.get(url, headers: headers) do
        {:ok, %{status: 200, body: items}} when is_list(items) ->
          {:ok, Enum.map(items, &parse_api_item/1)}

        _ ->
          {:error, :request_failed}
      end
    end
  rescue
    _ -> {:error, :request_failed}
  end

  defp get_collection_key(name_or_key) do
    # If it looks like a key (alphanumeric, 8 chars), use directly
    if Regex.match?(~r/^[A-Z0-9]{8}$/i, name_or_key) do
      {:ok, name_or_key}
    else
      # Search by name
      case list_collections() do
        {:ok, collections} ->
          match =
            Enum.find(collections, fn c ->
              String.downcase(c.name || "") == String.downcase(name_or_key)
            end)

          if match, do: {:ok, match.key}, else: {:error, :not_found}

        error ->
          error
      end
    end
  end

  # ============================================================
  # PRIVATE - PARSING
  # ============================================================

  defp parse_api_item(item) do
    data = item["data"] || item

    %{
      key: item["key"] || data["key"],
      title: data["title"] || "",
      authors: parse_creators(data["creators"] || []),
      year: extract_year(data["date"]),
      item_type: data["itemType"] || "unknown",
      abstract: data["abstractNote"],
      doi: data["DOI"],
      url: data["url"],
      publication: data["publicationTitle"] || data["bookTitle"],
      tags: Enum.map(data["tags"] || [], fn t -> t["tag"] || t end)
    }
  end

  defp parse_creators(creators) do
    Enum.map(creators, fn c ->
      case c do
        %{"lastName" => last, "firstName" => first} -> "#{last}, #{String.first(first || "")}."
        %{"name" => name} -> name
        _ -> "Unknown"
      end
    end)
  end

  defp extract_year(nil), do: nil

  defp extract_year(date) when is_binary(date) do
    case Regex.run(~r/(\d{4})/, date) do
      [_, year] -> String.to_integer(year)
      nil -> nil
    end
  end

  defp extract_year(_), do: nil

  defp parse_citation(citation) do
    # Match patterns like "Smith, 2020", "Smith & Jones, 2019", "Smith et al., 2020"
    patterns = [
      ~r/([A-Z][a-zA-Z'-]+(?:\s+et\s+al\.?)?),?\s*(\d{4})/i,
      ~r/([A-Z][a-zA-Z'-]+)\s+(?:and|&)\s+[A-Z][a-zA-Z'-]+,?\s*(\d{4})/i,
      ~r/([A-Z][a-zA-Z'-]+)\s+\((\d{4})\)/i
    ]

    result =
      Enum.find_value(patterns, fn pattern ->
        case Regex.run(pattern, citation) do
          [_, author, year] -> {author, year}
          nil -> nil
        end
      end)

    case result do
      {author, year} -> {:ok, author, year}
      nil -> :error
    end
  end

  defp matches_citation?(item, author, year) do
    # Check if item matches the citation
    item_year = to_string(item.year)
    year_str = to_string(year)

    year_matches = item_year == year_str

    author_matches =
      Enum.any?(item.authors, fn item_author ->
        String.contains?(String.downcase(item_author), String.downcase(author))
      end)

    year_matches and author_matches
  end

  defp get_suitable_tags do
    case Settings.get([:zotero, :suitable_tags]) do
      nil -> ["suitable", "recommended", "approved"]
      tags -> tags
    end
  end
end
