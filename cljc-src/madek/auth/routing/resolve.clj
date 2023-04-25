(ns madek.auth.routing.resolve
  (:require 
    [madek.auth.resources.sign-in.auth-systems.main :as sign-in-auth-systems]
    ))

(def resolve-table
  {:sign-in-auth-systems  #'sign-in-auth-systems/handler
   })

