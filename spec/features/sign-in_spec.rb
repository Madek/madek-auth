require 'spec_helper'

feature 'Sign in' do

  let :ext_auth_port do
    ENV['MADE_EXT_AUTH_PORT'] || '3167'
  end

  let :ext_auth_key_pair do 
    ECKey.new 
  end

  let :ext_auth_id do
    'ext-test-auth-sys'
  end

  let :ext_auth_name do
    'External Test Authentication System'
  end

  before :each do

    File.write('./tmp/private_key.pem', ext_auth_key_pair.private_key)
    File.write('./tmp/public_key.pem', ext_auth_key_pair.public_key)

    @auth_system = FactoryBot.create :auth_system,
      id: ext_auth_id,
      name: ext_auth_name,
      external_sign_in_url: "http://localhost:#{ext_auth_port}/sign-in",
      external_public_key: ext_auth_key_pair.public_key,
      external_private_key: nil,
      enabled: true

    @user = FactoryBot.create :user

    @auth_system.users << @user


  end

  scenario 'Sign-in' do
    visit '/auth/sign-in?return-to=%2Fauth%2Finfo&foo=42'
    fill_in 'email', with: @user.email
    click_on 'Continue'
    click_on ext_auth_name

    binding.pry
  end


end


