(ns madek.auth.routing.resolve
  (:require 
    [madek.auth.resources.sign-in.main :as sign-in]
    [madek.auth.resources.auth.main :as auth]
    ))

(def routes-resources
  {:auth auth/components
   :sign-in sign-in/components})
