// eTMA Handler - Main JavaScript Entry Point
// Phoenix LiveView + Custom Hooks

import "phoenix_html"
import { Socket } from "phoenix"
import { LiveSocket } from "phoenix_live_view"
import topbar from "../vendor/topbar"

// --- Custom LiveView Hooks ---

let Hooks = {}

/**
 * Calculator Hook
 * Provides real-time calculation preview for mark inputs
 */
Hooks.Calculator = {
  mounted() {
    this.el.addEventListener("input", (e) => {
      const expression = e.target.value
      const resultEl = this.el.parentElement.querySelector(".calculator-result")

      if (resultEl) {
        try {
          // Safe evaluation (only numbers and operators)
          if (/^[\d\+\-\*\/\(\)\.\s]+$/.test(expression)) {
            const result = this.safeEval(expression)
            resultEl.textContent = `= ${result}`
            resultEl.classList.remove("text-red-700", "bg-red-50")
            resultEl.classList.add("text-green-700", "bg-green-50")
          }
        } catch {
          resultEl.textContent = "Error"
          resultEl.classList.remove("text-green-700", "bg-green-50")
          resultEl.classList.add("text-red-700", "bg-red-50")
        }
      }
    })
  },

  // Simple safe evaluation for basic arithmetic
  safeEval(expr) {
    // Remove spaces
    expr = expr.replace(/\s+/g, '')

    // Simple tokenizer and calculator for +, -, *, /
    let tokens = expr.match(/(\d+\.?\d*|[\+\-\*\/\(\)])/g)
    if (!tokens) return 0

    // Very simple evaluation (no parentheses handling for now)
    let result = parseFloat(tokens[0]) || 0
    for (let i = 1; i < tokens.length; i += 2) {
      let op = tokens[i]
      let num = parseFloat(tokens[i + 1]) || 0
      switch (op) {
        case '+': result += num; break
        case '-': result -= num; break
        case '*': result *= num; break
        case '/': result = num !== 0 ? result / num : 0; break
      }
    }
    return Math.round(result * 100) / 100
  }
}

/**
 * AutoSave Hook
 * Triggers save after user stops typing
 */
Hooks.AutoSave = {
  mounted() {
    this.timeout = null

    this.el.addEventListener("input", () => {
      clearTimeout(this.timeout)
      this.timeout = setTimeout(() => {
        this.pushEvent("auto_save", {})
      }, 2000) // Save after 2 seconds of inactivity
    })
  },

  destroyed() {
    clearTimeout(this.timeout)
  }
}

/**
 * KeyboardShortcuts Hook
 * Handles keyboard shortcuts for power users
 */
Hooks.KeyboardShortcuts = {
  mounted() {
    this.handleKeydown = (e) => {
      // Ctrl+S or Cmd+S to save
      if ((e.ctrlKey || e.metaKey) && e.key === 's') {
        e.preventDefault()
        this.pushEvent("save", {})
      }

      // Ctrl+Enter to finalise
      if ((e.ctrlKey || e.metaKey) && e.key === 'Enter') {
        e.preventDefault()
        this.pushEvent("finalise", {})
      }

      // Escape to deselect
      if (e.key === 'Escape') {
        this.pushEvent("deselect", {})
      }
    }

    document.addEventListener("keydown", this.handleKeydown)
  },

  destroyed() {
    document.removeEventListener("keydown", this.handleKeydown)
  }
}

/**
 * DragDrop Hook
 * Enables drag-and-drop for comment bank items
 */
Hooks.DragDrop = {
  mounted() {
    this.el.setAttribute("draggable", "true")

    this.el.addEventListener("dragstart", (e) => {
      e.dataTransfer.setData("text/plain", this.el.dataset.commentId)
      e.dataTransfer.effectAllowed = "copy"
    })
  }
}

/**
 * DropZone Hook
 * Handles drops from comment bank
 */
Hooks.DropZone = {
  mounted() {
    this.el.addEventListener("dragover", (e) => {
      e.preventDefault()
      e.dataTransfer.dropEffect = "copy"
      this.el.classList.add("ring-2", "ring-blue-400")
    })

    this.el.addEventListener("dragleave", () => {
      this.el.classList.remove("ring-2", "ring-blue-400")
    })

    this.el.addEventListener("drop", (e) => {
      e.preventDefault()
      this.el.classList.remove("ring-2", "ring-blue-400")

      const commentId = e.dataTransfer.getData("text/plain")
      if (commentId) {
        this.pushEvent("insert_comment", { id: commentId })
      }
    })
  }
}

// --- LiveSocket Configuration ---

let csrfToken = document.querySelector("meta[name='csrf-token']")?.getAttribute("content")
let liveSocket = new LiveSocket("/live", Socket, {
  params: { _csrf_token: csrfToken },
  hooks: Hooks,
  dom: {
    onBeforeElUpdated(from, to) {
      // Preserve focus on inputs during LiveView updates
      if (from._x_isShown !== undefined) {
        to._x_isShown = from._x_isShown
      }
      return true
    }
  }
})

// --- Progress Bar Configuration ---

// Show progress bar on live navigation and form submits
topbar.config({ barColors: { 0: "#3b82f6" }, shadowColor: "rgba(0, 0, 0, .3)" })
window.addEventListener("phx:page-loading-start", _info => topbar.show(300))
window.addEventListener("phx:page-loading-stop", _info => topbar.hide())

// --- Connect the LiveSocket ---

liveSocket.connect()

// Expose liveSocket on window for console debugging
window.liveSocket = liveSocket

// --- Global Event Handlers ---

// Handle flash message dismissal
document.addEventListener("click", (e) => {
  if (e.target.matches("[data-dismiss='flash']")) {
    e.target.closest(".alert")?.remove()
  }
})

// Keyboard accessibility - allow Enter key on buttons
document.addEventListener("keydown", (e) => {
  if (e.key === "Enter" && e.target.matches("button, [role='button']")) {
    e.target.click()
  }
})

console.log("eTMA Handler loaded successfully")
