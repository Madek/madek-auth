(ns madek.auth.routing.resolve
  (:require 
    [madek.auth.resources.sign-in.main :as sign-in]
    [madek.auth.resources.sign-in.auth-systems.main :as sign-in-auth-systems]
    [madek.auth.resources.auth.main :as auth]
    ))

(def routes-resources
  {:auth auth/components
   :sign-in sign-in/components
   :sign-in-auth-systems sign-in-auth-systems/components
   })
