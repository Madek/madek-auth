require 'active_support/all'
require 'addressable/uri'
require 'pry'
require 'jwt'
require 'optparse'
require 'sinatra/base'
require 'pathname'



### opts #######################################################################

$options = {
  port: ENV['PORT'].presence || '3167',
  private_key_file: Pathname.new(ENV['PRIVATE_KEY_FILE'].presence || "tmp/private_key.pem" ),
  public_key_file: Pathname.new(ENV['PUBLIC_KEY_FILE'].presence || "tmp/public_key.pem" )
}


def parse 
  OptionParser.new do |parser|
    parser.banner = "test-auth-system [options]"

    parser.on("--public-key-file=PUBLIC_KEY_FILE") do |fn|
      $options[:public_key_file] = Pathname.new(fn)
    end

    parser.on("--private-key-file=PRIVATE_KEY_FILE") do |fn|
      $options[:private_key_file] = Pathname.new(fn)
    end

    parser.on("--port=PORT") do |p|
      $options[:port] = p
    end

    parser.on("-h", "--help", "Print help") do
      puts parser
      puts "current options:"
      puts $options
      exit 0
    end
  end.parse! 
end

parse 



### key helpers ################################################################


def public_key
  OpenSSL::PKey.read(IO.read($options[:public_key_file]))
end

def private_key
  OpenSSL::PKey.read(IO.read($options[:private_key_file]))
end



### service ####################################################################

class MadekAuthService < Sinatra::Application
  set :port, $options[:port]

  get '/' do
    'Hello world!'
  end

  run!
end



