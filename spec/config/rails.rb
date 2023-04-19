require 'rails/all'

module Madek
  class Application < Rails::Application
    config.eager_load = false

    config.autoload_paths += [
      Rails.root.join('datalayer', 'lib'),
      Rails.root.join('datalayer', 'app', 'models'),
      Rails.root.join('datalayer', 'app', 'lib')
    ]
  end
end


Rails.application.initialize!
