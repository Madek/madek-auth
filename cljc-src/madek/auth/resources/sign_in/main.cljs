(ns madek.auth.resources.sign-in.main
  (:require
    [reagent.core :as reagent :rename {atom ratom}]
    [madek.auth.html.forms.core]
    [taoensso.timbre :refer [debug error info spy warn]]))


(def data* (ratom {}))

(defn page []
  [:div
   [:h1 "Sign-in"]])


(def components 
  {:page page})
