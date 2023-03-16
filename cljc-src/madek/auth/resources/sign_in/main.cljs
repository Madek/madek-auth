(ns madek.auth.resources.sign-in.main
  (:require
    [taoensso.timbre :refer [debug error info spy warn]]))


(defn page []
  [:div
  [:h1 "Sign-in"]])


(def components 
  {:page page})
