(ns madek.auth.routing.resolve
  (:require 
    [madek.auth.resources.sign-in.main :as sign-in]
    ))

(def routes-resources
  {:sign-in sign-in/components})
