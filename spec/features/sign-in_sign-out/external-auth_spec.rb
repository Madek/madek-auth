require 'spec_helper'

feature 'Sign in / sign out via ext auth', ci_group: :extauth do

  let :ext_auth_port do
    ENV['TEST_AUTH_SYSTEM_PORT'] || '3167'
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

    @auth_system = FactoryBot.create :auth_system,
      id: ext_auth_id,
      name: ext_auth_name,
      external_sign_in_url: "http://localhost:#{ext_auth_port}/sign-in",
      external_public_key: ext_auth_key_pair.public_key,
      external_private_key: nil,
      enabled: true


    File.write('./tmp/private_key.pem', ext_auth_key_pair.private_key)
    File.write('./tmp/public_key.pem', @auth_system.internal_public_key)

    @user = FactoryBot.create :user

    @auth_system.users << @user


  end

  scenario 'Successfull sign-in' do
    visit '/auth/sign-in?return-to=%2Fauth%2Finfo&foo=42'
    fill_in 'email', with: @user.email
    click_on 'Continue'
    click_on ext_auth_name
    click_on "Yes"
    # redirecting and full reload takes some time; somewhat dirty but more easy
    # to debug than wait_until
    sleep(0.5) 
    uri = Addressable::URI.parse(current_url)
    # we are on the supplied return-to path:
    expect(uri.path).to be== '/auth/info'

    # check some content:
    expect{find("code.user-session-data")}.not_to raise_error 
    expect{YAML.load(find("code.user-session-data").text)}.not_to raise_error 
    user_session_data = YAML.load(find("code.user-session-data").text).with_indifferent_access
    expect(user_session_data[:person_last_name]).to be== @user.person.last_name
    user_session_id = user_session_data[:session_id]
    expect(user_session_id).to be
    expect{UserSession.find(user_session_id)}.not_to raise_error

    click_on @user.person.last_name
    find("form button", text: 'Sign out').click
    expect(current_path).to be== '/'
    expect{UserSession.find(user_session_id)}.to raise_error ActiveRecord::RecordNotFound
    
  end

  scenario 'Unsucessfull sign-in: the auth-service returns false' do
    visit '/auth/sign-in?return-to=%2Fauth%2Finfo&foo=42'
    fill_in 'email', with: @user.email
    click_on 'Continue'
    click_on ext_auth_name
    click_on "No"
    expect(page).to have_content "Authentication failed"
    expect(page).to have_content "The user did not authenticate successfully!"
    expect(page).to have_content "Start over"
    click_on "Start over"
    uri = Addressable::URI.parse(current_url)
    # return-to should be preserved during this process:
    expect(uri.query).to be== 'return-to=%2Fauth%2Finfo'
  end

  scenario 'Try to sign in with a deactivated account' do
    @user.update! is_deactivated: true
    visit '/auth/sign-in'
    fill_in 'email', with: @user.email
    click_on 'Continue'
    expect(page).to have_content "There is an registered account but it is deactivated."
  end

end
