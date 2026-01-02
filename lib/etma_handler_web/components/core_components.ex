# SPDX-License-Identifier: AGPL-3.0-or-later
defmodule EtmaHandlerWeb.CoreComponents do
  @moduledoc """
  Core UI Components for eTMA Handler.

  Provides high-contrast, accessible components following WCAG 2.3 guidelines.
  Includes the signature "Smart Calculator" input that evaluates expressions.
  """

  use Phoenix.Component
  alias Phoenix.LiveView.JS

  # --- Layout Components ---

  @doc """
  Renders flash messages.
  """
  attr :id, :string, doc: "the optional id of flash container"
  attr :flash, :map, default: %{}, doc: "the map of flash messages to display"
  attr :title, :string, default: nil
  attr :kind, :atom, values: [:info, :error], doc: "used for styling and aria attributes"
  attr :rest, :global, doc: "the arbitrary HTML attributes to add to the flash container"

  slot :inner_block, doc: "the optional inner block that renders the flash message"

  def flash(assigns) do
    assigns = assign_new(assigns, :id, fn -> "flash-#{assigns.kind}" end)

    ~H"""
    <div
      :if={msg = render_slot(@inner_block) || Phoenix.Flash.get(@flash, @kind)}
      id={@id}
      phx-click={JS.push("lv:clear-flash", value: %{key: @kind}) |> hide("##{@id}")}
      role="alert"
      class={[
        "fixed top-4 right-4 z-50 w-96 p-4 rounded-lg shadow-lg border-2",
        @kind == :info && "bg-blue-50 text-blue-900 border-blue-200",
        @kind == :error && "bg-red-50 text-red-900 border-red-200"
      ]}
      {@rest}
    >
      <p :if={@title} class="font-bold text-sm mb-1"><%= @title %></p>
      <p class="text-sm"><%= msg %></p>
      <button type="button" class="absolute top-2 right-2" aria-label="close">
        <.icon name="hero-x-mark-solid" class="h-5 w-5 opacity-60 hover:opacity-100" />
      </button>
    </div>
    """
  end

  @doc """
  Shows the flash group with standard styling.
  """
  attr :flash, :map, required: true, doc: "the map of flash messages"
  attr :id, :string, default: "flash-group", doc: "the optional id of flash container"

  def flash_group(assigns) do
    ~H"""
    <div id={@id}>
      <.flash kind={:info} title="Success!" flash={@flash} />
      <.flash kind={:error} title="Error!" flash={@flash} />
      <.flash
        id="client-error"
        kind={:error}
        title="Connection Lost"
        phx-disconnected={show(".phx-client-error #client-error")}
        phx-connected={hide("#client-error")}
        hidden
      >
        Attempting to reconnect...
      </.flash>
      <.flash
        id="server-error"
        kind={:error}
        title="Server Error"
        phx-disconnected={show(".phx-server-error #server-error")}
        phx-connected={hide("#server-error")}
        hidden
      >
        Please refresh the page.
      </.flash>
    </div>
    """
  end

  # --- Form Components ---

  @doc """
  Renders a simple form.
  """
  attr :for, :any, required: true, doc: "the datastructure for the form"
  attr :as, :any, default: nil, doc: "the server side parameter to collect all input under"

  attr :rest, :global,
    include: ~w(autocomplete name rel action enctype method novalidate target multipart),
    doc: "the arbitrary HTML attributes to apply to the form tag"

  slot :inner_block, required: true
  slot :actions, doc: "the slot for form actions, such as a submit button"

  def simple_form(assigns) do
    ~H"""
    <.form :let={f} for={@for} as={@as} {@rest}>
      <div class="space-y-6">
        <%= render_slot(@inner_block, f) %>
        <div :for={action <- @actions} class="mt-6 flex items-center justify-end gap-4">
          <%= render_slot(action, f) %>
        </div>
      </div>
    </.form>
    """
  end

  @doc """
  Renders an input with label and error messages.
  """
  attr :id, :any, default: nil
  attr :name, :any
  attr :label, :string, default: nil
  attr :value, :any

  attr :type, :string,
    default: "text",
    values: ~w(checkbox color date datetime-local email file hidden month number password
               range radio search select tel text textarea time url week calculator)

  attr :field, Phoenix.HTML.FormField,
    doc: "a form field struct retrieved from the form, for example: @form[:email]"

  attr :errors, :list, default: []
  attr :checked, :boolean, doc: "the checked flag for checkbox inputs"
  attr :prompt, :string, default: nil, doc: "the prompt for select inputs"
  attr :options, :list, doc: "the options to pass to Phoenix.HTML.Form.options_for_select/2"
  attr :multiple, :boolean, default: false, doc: "the multiple flag for select inputs"
  attr :max_score, :integer, default: nil, doc: "the maximum score for calculator inputs"

  attr :rest, :global,
    include: ~w(accept autocomplete capture cols disabled form list max maxlength min minlength
                multiple pattern placeholder readonly required rows size step)

  slot :inner_block

  def input(%{field: %Phoenix.HTML.FormField{} = field} = assigns) do
    assigns
    |> assign(field: nil, id: assigns.id || field.id)
    |> assign(:errors, Enum.map(field.errors, &translate_error(&1)))
    |> assign_new(:name, fn -> if assigns.multiple, do: field.name <> "[]", else: field.name end)
    |> assign_new(:value, fn -> field.value end)
    |> input()
  end

  def input(%{type: "calculator"} = assigns) do
    ~H"""
    <div phx-feedback-for={@name}>
      <.label :if={@label} for={@id}>
        <%= @label %>
        <span :if={@max_score} class="text-xs font-normal text-slate-500 ml-2">
          (Max: <%= @max_score %>)
        </span>
      </.label>
      <div class="relative mt-1">
        <input
          type="text"
          name={@name}
          id={@id}
          value={@value}
          class={[
            "block w-full text-lg font-mono p-3 rounded-lg shadow-sm",
            "border-slate-300 focus:border-blue-600 focus:ring-blue-600",
            "bg-white text-slate-900",
            @errors != [] && "border-red-400 focus:border-red-600 focus:ring-red-600"
          ]}
          placeholder="e.g. 10 + 5"
          phx-debounce="300"
          {@rest}
        />
        <div class="absolute right-3 top-3.5">
          <%= case calculate_safe(@value) do %>
            <% {:ok, result} -> %>
              <span class="font-bold text-green-700 bg-green-50 px-2 py-0.5 rounded border border-green-200 text-sm">
                = <%= result %>
              </span>
            <% {:error, _} -> %>
              <span class="font-bold text-red-700 bg-red-50 px-2 py-0.5 rounded border border-red-200 text-sm">
                Error
              </span>
          <% end %>
        </div>
      </div>
      <.error :for={msg <- @errors}><%= msg %></.error>
    </div>
    """
  end

  def input(%{type: "checkbox"} = assigns) do
    assigns =
      assign_new(assigns, :checked, fn ->
        Phoenix.HTML.Form.normalize_value("checkbox", assigns[:value])
      end)

    ~H"""
    <div phx-feedback-for={@name}>
      <label class="flex items-center gap-4 text-sm leading-6 text-slate-700">
        <input type="hidden" name={@name} value="false" />
        <input
          type="checkbox"
          id={@id}
          name={@name}
          value="true"
          checked={@checked}
          class="rounded border-slate-300 text-blue-600 focus:ring-blue-600"
          {@rest}
        />
        <%= @label %>
      </label>
      <.error :for={msg <- @errors}><%= msg %></.error>
    </div>
    """
  end

  def input(%{type: "select"} = assigns) do
    ~H"""
    <div phx-feedback-for={@name}>
      <.label for={@id}><%= @label %></.label>
      <select
        id={@id}
        name={@name}
        class="mt-2 block w-full rounded-lg border border-slate-300 bg-white shadow-sm focus:border-blue-600 focus:ring-blue-600 text-sm"
        multiple={@multiple}
        {@rest}
      >
        <option :if={@prompt} value=""><%= @prompt %></option>
        <%= Phoenix.HTML.Form.options_for_select(@options, @value) %>
      </select>
      <.error :for={msg <- @errors}><%= msg %></.error>
    </div>
    """
  end

  def input(%{type: "textarea"} = assigns) do
    ~H"""
    <div phx-feedback-for={@name}>
      <.label for={@id}><%= @label %></.label>
      <textarea
        id={@id}
        name={@name}
        class={[
          "mt-2 block w-full rounded-lg text-slate-900 focus:ring-blue-600 text-sm p-3",
          "min-h-[6rem] border-slate-300 focus:border-blue-600",
          @errors != [] && "border-red-400 focus:border-red-600 focus:ring-red-600"
        ]}
        {@rest}
      ><%= Phoenix.HTML.Form.normalize_value("textarea", @value) %></textarea>
      <.error :for={msg <- @errors}><%= msg %></.error>
    </div>
    """
  end

  # All other inputs
  def input(assigns) do
    ~H"""
    <div phx-feedback-for={@name}>
      <.label for={@id}><%= @label %></.label>
      <input
        type={@type}
        name={@name}
        id={@id}
        value={Phoenix.HTML.Form.normalize_value(@type, @value)}
        class={[
          "mt-2 block w-full rounded-lg text-slate-900 focus:ring-blue-600 text-sm p-3",
          "border-slate-300 focus:border-blue-600",
          @errors != [] && "border-red-400 focus:border-red-600 focus:ring-red-600"
        ]}
        {@rest}
      />
      <.error :for={msg <- @errors}><%= msg %></.error>
    </div>
    """
  end

  @doc """
  Renders a label.
  """
  attr :for, :string, default: nil
  slot :inner_block, required: true

  def label(assigns) do
    ~H"""
    <label for={@for} class="block text-sm font-bold leading-6 text-slate-800">
      <%= render_slot(@inner_block) %>
    </label>
    """
  end

  @doc """
  Generates a generic error message.
  """
  slot :inner_block, required: true

  def error(assigns) do
    ~H"""
    <p class="mt-1 flex gap-2 text-sm leading-6 text-red-600">
      <.icon name="hero-exclamation-circle-mini" class="mt-0.5 h-5 w-5 flex-none" />
      <%= render_slot(@inner_block) %>
    </p>
    """
  end

  # --- Button Components ---

  @doc """
  Renders a button.
  """
  attr :type, :string, default: nil
  attr :class, :string, default: nil
  attr :variant, :string, default: "primary", values: ~w(primary secondary danger)
  attr :rest, :global, include: ~w(disabled form name value)

  slot :inner_block, required: true

  def button(assigns) do
    ~H"""
    <button
      type={@type}
      class={[
        "phx-submit-loading:opacity-75 rounded-lg py-2 px-4",
        "text-sm font-bold leading-6 shadow-sm transition-all",
        "focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2",
        @variant == "primary" &&
          "bg-blue-700 hover:bg-blue-800 text-white active:text-white/80 focus-visible:outline-blue-600",
        @variant == "secondary" &&
          "bg-slate-100 hover:bg-slate-200 text-slate-700 focus-visible:outline-slate-600",
        @variant == "danger" &&
          "bg-red-600 hover:bg-red-700 text-white focus-visible:outline-red-600",
        @class
      ]}
      {@rest}
    >
      <%= render_slot(@inner_block) %>
    </button>
    """
  end

  # --- Header Components ---

  @doc """
  Renders a header with title.
  """
  attr :class, :string, default: nil

  slot :inner_block, required: true
  slot :subtitle
  slot :actions

  def header(assigns) do
    ~H"""
    <header class={[@actions != [] && "flex items-center justify-between gap-6", @class]}>
      <div>
        <h1 class="text-lg font-bold leading-8 text-slate-800">
          <%= render_slot(@inner_block) %>
        </h1>
        <p :if={@subtitle != []} class="mt-1 text-sm leading-6 text-slate-600">
          <%= render_slot(@subtitle) %>
        </p>
      </div>
      <div class="flex-none"><%= render_slot(@actions) %></div>
    </header>
    """
  end

  # --- Icon Component ---

  @doc """
  Renders a Heroicon.
  """
  attr :name, :string, required: true
  attr :class, :string, default: nil

  def icon(%{name: "hero-" <> _} = assigns) do
    ~H"""
    <span class={[@name, @class]} />
    """
  end

  # --- Helper Functions ---

  defp calculate_safe(nil), do: {:ok, 0}
  defp calculate_safe(""), do: {:ok, 0}

  defp calculate_safe(value) when is_binary(value) do
    EtmaHandler.Logic.Calculator.evaluate(value)
  end

  defp calculate_safe(value) when is_number(value), do: {:ok, value}

  defp translate_error({msg, opts}) do
    Enum.reduce(opts, msg, fn {key, value}, acc ->
      String.replace(acc, "%{#{key}}", fn _ -> to_string(value) end)
    end)
  end

  defp hide(js \\ %JS{}, selector) do
    JS.hide(js,
      to: selector,
      time: 200,
      transition:
        {"transition-all transform ease-in duration-200",
         "opacity-100 translate-y-0 sm:scale-100",
         "opacity-0 translate-y-4 sm:translate-y-0 sm:scale-95"}
    )
  end

  defp show(js \\ %JS{}, selector) do
    JS.show(js,
      to: selector,
      transition:
        {"transition-all transform ease-out duration-300",
         "opacity-0 translate-y-4 sm:translate-y-0 sm:scale-95",
         "opacity-100 translate-y-0 sm:scale-100"}
    )
  end
end
