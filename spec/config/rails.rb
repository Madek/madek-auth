require 'rails/all'

ENV['RAILS_ENV'] = ENV['RAILS_ENV'].presence || 'test'

module Madek
  class Application < Rails::Application
    config.eager_load = false

    config.autoload_paths += [
      Rails.root.join('datalayer', 'lib'),
      Rails.root.join('datalayer', 'app', 'models'),
      Rails.root.join('datalayer', 'app', 'lib')
    ]

    config.paths['config/database'] = ['datalayer/config/database.yml']
  end
end


Rails.application.initialize!
