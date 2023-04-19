require 'open3'

AuthSystem.class_eval do
  attr_accessor :external_private_key
end

FactoryBot.define do
  factory :auth_system do
    type {"external"}
    name {Faker::Company.name}
    id {name.downcase}

    internal_private_key {
      Open3.popen3("openssl ecparam -name prime256v1 -genkey -noout") {
        |stdin, stdout, stderr|
        stdout.read }}

    internal_public_key {
      Open3.popen3("openssl ec -pubout") {
        |stdin, stdout, stderr|
        stdin.write(internal_private_key)
        stdin.flush
        stdin.close
        stdout.read}}

    external_private_key {
      Open3.popen3("openssl ecparam -name prime256v1 -genkey -noout") {
        |stdin, stdout, stderr|
        stdout.read }}

    external_public_key {
      Open3.popen3("openssl ec -pubout") {
        |stdin, stdout, stderr|
        stdin.write(external_private_key)
        stdin.flush
        stdin.close
        stdout.read}}

  end
end
