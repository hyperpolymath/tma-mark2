// eTMA Handler - Main ReScript Entry Point
// Phoenix LiveView + Custom Hooks

@val external document: Dom.document = "document"
@send external querySelector: (Dom.document, string) => Nullable.t<Dom.element> = "querySelector"
@send external getAttribute: (Dom.element, string) => Nullable.t<string> = "getAttribute"
@send external addEventListener: (Dom.element, string, Dom.event => unit) => unit = "addEventListener"
@send external addEventListenerDoc: (Dom.document, string, Dom.event => unit) => unit = "addEventListener"
@send external removeEventListener: (Dom.document, string, Dom.event => unit) => unit = "removeEventListener"
@send external setAttribute: (Dom.element, string, string) => unit = "setAttribute"
@send external closest: (Dom.element, string) => Nullable.t<Dom.element> = "closest"
@send external remove: Dom.element => unit = "remove"
@send external matches: (Dom.element, string) => bool = "matches"
@send external click: Dom.element => unit = "click"
@get external target: Dom.event => Dom.element = "target"
@get external key: Dom.event => string = "key"
@get external ctrlKey: Dom.event => bool = "ctrlKey"
@get external metaKey: Dom.event => bool = "metaKey"
@send external preventDefault: Dom.event => unit = "preventDefault"
@get external parentElement: Dom.element => Nullable.t<Dom.element> = "parentElement"
@get external value: Dom.element => string = "value"
@set external textContent: (Dom.element, string) => unit = "textContent"
@get external classList: Dom.element => {..} = "classList"
@send external classAdd: ({..}, string, string) => unit = "add"
@send external classRemove: ({..}, string, string) => unit = "remove"
@get external dataset: Dom.element => Js.Dict.t<string> = "dataset"
@get external dataTransfer: Dom.event => {..} = "dataTransfer"
@send external setData: ({..}, string, string) => unit = "setData"
@send external getData: ({..}, string) => string = "getData"
@set external effectAllowed: ({..}, string) => unit = "effectAllowed"
@set external dropEffect: ({..}, string) => unit = "dropEffect"

@val external setTimeout: (unit => unit, int) => int = "setTimeout"
@val external clearTimeout: int => unit = "clearTimeout"
@val @scope("console") external log: string => unit = "log"
@val @scope("Math") external round: float => float = "round"
@val @scope("Number") external parseFloat: string => float = "parseFloat"

// Safe evaluation for calculator
let safeEval = expr => {
  let cleaned = expr->Js.String.replaceByRe(%re("/\s+/g"), "")
  let tokens = cleaned->Js.String.match_(%re("/(\d+\.?\d*|[\+\-\*\/\(\)])/g"))
  
  switch tokens {
  | None => 0.0
  | Some(t) => {
      let arr = t->Array.map(s => s)
      if Array.length(arr) == 0 {
        0.0
      } else {
        let result = ref(parseFloat(arr[0]->Option.getOr("0")))
        let i = ref(1)
        while i.contents < Array.length(arr) {
          let op = arr[i.contents]->Option.getOr("")
          let num = parseFloat(arr[i.contents + 1]->Option.getOr("0"))
          switch op {
          | "+" => result := result.contents +. num
          | "-" => result := result.contents -. num
          | "*" => result := result.contents *. num
          | "/" => result := num != 0.0 ? result.contents /. num : 0.0
          | _ => ()
          }
          i := i.contents + 2
        }
        round(result.contents *. 100.0) /. 100.0
      }
    }
  }
}

// Hook definitions
type hookContext
@send external pushEvent: (hookContext, string, {..}) => unit = "pushEvent"

let calculatorMounted = (el: Dom.element, _ctx: hookContext) => {
  el->addEventListener("input", e => {
    let expression = (e->target)->value
    el->parentElement->Nullable.toOption->Option.forEach(parent => {
      parent->querySelector(".calculator-result")->Nullable.toOption->Option.forEach(resultEl => {
        if Js.Re.test_(%re("/^[\d\+\-\*\/\(\)\.\s]+$/"), expression) {
          try {
            let result = safeEval(expression)
            resultEl->textContent = `= ${Float.toString(result)}`
            let cl = resultEl->classList
            cl->classRemove("text-red-700", "bg-red-50")
            cl->classAdd("text-green-700", "bg-green-50")
          } catch {
          | _ => {
              resultEl->textContent = "Error"
              let cl = resultEl->classList
              cl->classRemove("text-green-700", "bg-green-50")
              cl->classAdd("text-red-700", "bg-red-50")
            }
          }
        }
      })
    })
  })
}

// Global event handlers
let initGlobalHandlers = () => {
  // Flash message dismissal
  document->addEventListenerDoc("click", e => {
    let target = e->target
    if target->matches("[data-dismiss='flash']") {
      target->closest(".alert")->Nullable.toOption->Option.forEach(el => el->remove)
    }
  })
  
  // Keyboard accessibility
  document->addEventListenerDoc("keydown", e => {
    if e->key == "Enter" && (e->target)->matches("button, [role='button']") {
      (e->target)->click
    }
  })
}

// Initialize
let init = () => {
  initGlobalHandlers()
  log("eTMA Handler loaded successfully (ReScript)")
}

let _ = init()
