#!/usr/bin/env julia
# SPDX-License-Identifier: MIT
#
# SKETCH: Convert SCM comment bank to AsciiDoc reference document
#
# This is a demonstration of how comment banks could be processed.
# In production, would use a proper Scheme parser.
#
# Usage: julia convert-to-adoc.jl computing-fundamentals.scm

"""
Simple representation of a comment bank.
In production, would parse actual SCM S-expressions.
"""
struct CommentBank
    name::String
    module::String
    version::String
    categories::Vector{Category}
end

struct Category
    name::String
    description::String
    icon::String
    comments::Vector{Comment}
end

struct Comment
    id::String
    shortcut::String
    severity::String
    text::String
    variants::Dict{String, String}
end

"""
Generate AsciiDoc from a comment bank.
"""
function to_asciidoc(bank::CommentBank)::String
    io = IOBuffer()

    # Header
    println(io, "= $(bank.name) - Comment Bank Reference")
    println(io, ":toc: left")
    println(io, ":toclevels: 2")
    println(io, ":icons: font")
    println(io, ":source-highlighter: rouge")
    println(io)
    println(io, "Module:: $(bank.module)")
    println(io, "Version:: $(bank.version)")
    println(io, "Generated:: $(Dates.today())")
    println(io)

    # Quick reference table
    println(io, "== Quick Reference")
    println(io)
    println(io, "[cols=\"1,2,4\", options=\"header\"]")
    println(io, "|===")
    println(io, "|Shortcut |Category |Preview")
    for cat in bank.categories
        for comment in cat.comments
            preview = first(comment.text, 50) * (length(comment.text) > 50 ? "..." : "")
            println(io, "|`/$(comment.shortcut)` |$(cat.name) |$(preview)")
        end
    end
    println(io, "|===")
    println(io)

    # Full comments by category
    for cat in bank.categories
        println(io, "== $(cat.icon) $(titlecase(cat.name))")
        println(io)
        println(io, "_$(cat.description)_")
        println(io)

        for comment in cat.comments
            println(io, "=== $(comment.id): $(comment.shortcut)")
            println(io)
            println(io, "[horizontal]")
            println(io, "Shortcut:: `/$(comment.shortcut)` or `;;$(replace(comment.shortcut, "-" => ""))`")
            if !isempty(comment.severity)
                severity_icon = comment.severity == "high" ? "🔴" :
                               comment.severity == "medium" ? "🟡" : "🟢"
                println(io, "Severity:: $(severity_icon) $(comment.severity)")
            end
            println(io)

            if !isempty(comment.variants)
                println(io, ".Variants")
                println(io, "[cols=\"1,4\"]")
                println(io, "|===")
                for (variant, text) in comment.variants
                    println(io, "|*$(variant)* |$(text)")
                end
                println(io, "|===")
            else
                println(io, "[quote]")
                println(io, "____")
                println(io, comment.text)
                println(io, "____")
            end
            println(io)
        end
    end

    return String(take!(io))
end

"""
SKETCH: Parse SCM file (simplified - real impl would use proper S-expr parser)
"""
function parse_scm(filename::String)::CommentBank
    # This is a placeholder - in production would use:
    # - A proper Scheme/S-expression parser
    # - Or call out to Guile to read the file

    # For now, return example data
    return CommentBank(
        "Computing Fundamentals",
        "TM129",
        "1.0.0",
        [
            Category(
                "referencing",
                "Comments about citation and reference formatting",
                "📚",
                [
                    Comment(
                        "ref-001",
                        "ref-missing",
                        "high",
                        "",
                        Dict(
                            "neutral" => "References should be included for all external sources used.",
                            "encouraging" => "You've made good points - now strengthen them by adding references.",
                            "concern" => "Several statements lack supporting references."
                        )
                    ),
                    Comment(
                        "ref-002",
                        "ref-harvard",
                        "medium",
                        "Please use Harvard referencing format consistently.",
                        Dict{String,String}()
                    )
                ]
            ),
            Category(
                "code-quality",
                "Comments about programming and code submissions",
                "💻",
                [
                    Comment(
                        "code-001",
                        "code-comments",
                        "medium",
                        "",
                        Dict(
                            "neutral" => "Code should include comments explaining the purpose.",
                            "encouraging" => "Your code works well. Adding comments would make it even better.",
                            "concern" => "The code lacks comments, making it difficult to follow."
                        )
                    )
                ]
            )
        ]
    )
end

# Main
using Dates

if abspath(PROGRAM_FILE) == @__FILE__
    if length(ARGS) < 1
        println("Usage: julia convert-to-adoc.jl <comment-bank.scm>")
        println()
        println("SKETCH: Demonstrates comment bank conversion pipeline.")
        println("In production, would properly parse the SCM file.")
        exit(1)
    end

    input_file = ARGS[1]
    output_file = replace(input_file, ".scm" => "-reference.adoc")

    println("Converting: $input_file → $output_file")

    bank = parse_scm(input_file)
    adoc = to_asciidoc(bank)

    open(output_file, "w") do f
        write(f, adoc)
    end

    println("Done! Generated $(length(bank.categories)) categories.")
end
