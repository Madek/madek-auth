require 'capybara/rspec'
require 'factory_bot'
require 'faker'
require 'pry'
require 'rspec'
require 'selenium-webdriver'
require 'logger'

require 'config/rails'
ENV['RAILS_ENV'] = ENV['RAILS_ENV'].presence || 'test'


$logger = logger = Logger.new(STDOUT)
logger.level = Logger::INFO


RSpec.configure do |config|

  config.before :all do
    @spec_seed = \
      ENV['SPEC_SEED'].presence.try(:strip) || `git log -n1 --format=%T`.strip
    $logger.info "SPEC_SEED='#{@spec_seed}'"
    srand Integer(@spec_seed, 16)
  end

  config.after :all do
    puts
    $logger.info "SPEC_SEED='#{@spec_seed}'"
  end

  config.include FactoryBot::Syntax::Methods
end

