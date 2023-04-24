require 'spec_helper'

feature 'Sign in' do

  let :ext_auth_port do
    ENV['MADE_EXT_AUTH_PORT'] || '3167'
  end

  let :ext_auth_key_pair do 
    ECKey.new 
  end

  before :each do

    File.write('./tmp/private_key.pem', ext_auth_key_pair.private_key)
    File.write('./tmp/public_key.pem', ext_auth_key_pair.public_key)

    @auth_system = FactoryBot.create :auth_system,
      external_public_key: ext_auth_key_pair.public_key,
      external_private_key: nil

  end

  scenario 'Sign-in' do
    visit '/auth/sign-in'
    binding.pry
  end


end


