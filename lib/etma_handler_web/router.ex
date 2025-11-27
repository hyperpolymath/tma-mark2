defmodule EtmaHandlerWeb.Router do
  use EtmaHandlerWeb, :router

  pipeline :browser do
    plug :accepts, ["html"]
    plug :fetch_session
    plug :fetch_live_flash
    plug :put_root_layout, html: {EtmaHandlerWeb.Layouts, :root}
    plug :protect_from_forgery
    plug :put_secure_browser_headers
  end

  pipeline :api do
    plug :accepts, ["json"]
  end

  scope "/", EtmaHandlerWeb do
    pipe_through :browser

    # Main marking interface (The "Cockpit")
    live "/", MarkingLive, :index
    live "/mark/:submission_id", MarkingLive, :mark

    # Course management
    live "/courses", CourseLive.Index, :index
    live "/courses/:id", CourseLive.Show, :show

    # Comment bank / Refinery
    live "/refinery", RefineryLive, :index

    # Settings
    live "/settings", SettingsLive, :index
  end

  # API routes (for CLI tools or integrations)
  scope "/api", EtmaHandlerWeb do
    pipe_through :api

    # Health check
    get "/health", ApiController, :health
  end

  # Enable LiveDashboard in development
  if Application.compile_env(:etma_handler, :dev_routes) do
    import Phoenix.LiveDashboard.Router

    scope "/dev" do
      pipe_through :browser

      live_dashboard "/dashboard", metrics: EtmaHandlerWeb.Telemetry
    end
  end
end
