(ns madek.auth.routing.resolve
  (:require 
    [madek.auth.resources.sign-in.auth-systems.main]
    [madek.auth.resources.sign-in.auth-systems.auth-system-request.main]))

(def resolve-table
  {:sign-in-auth-systems  
   #'madek.auth.resources.sign-in.auth-systems.main/handler
   :sign-in-auth-system-request 
   #'madek.auth.resources.sign-in.auth-systems.auth-system-request.main/handler
   })

