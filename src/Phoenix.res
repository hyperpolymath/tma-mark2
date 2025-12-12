// Phoenix LiveView bindings

// External imports (will be bundled)
@module("phoenix_html") external phoenixHtml: unit = "default"
@module("phoenix") external socket: {..} = "Socket"
@module("phoenix_live_view") external liveSocket: {..} = "LiveSocket"

type hook = {
  mounted: unit => unit,
  destroyed: option<unit => unit>,
}

type liveSocketConfig = {
  params: {..},
  hooks: Js.Dict.t<hook>,
  dom: option<{..}>,
}
