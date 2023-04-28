(ns madek.auth.resources.sign-in.auth-systems.auth-system-request.main
  (:require
    [cljs.core.async :refer [go go-loop]]
    [cljs.pprint :refer [pprint]]
    [madek.auth.html.forms.core :as forms]
    [madek.auth.html.icons :as icons]
    [madek.auth.http.client.core :as http-client]
    [madek.auth.routes :refer [navigate! path]]
    [madek.auth.state :as state :refer [debug?* hidden-routing-state-component]]
    [madek.auth.utils.core :refer [presence]]
    [reagent.core :as reagent :refer [reaction] :rename {atom ratom}]
    [taoensso.timbre :refer [debug error info spy warn]]))


(defn request []
  (info 'request)
  (go (some-> 
        {:method :post}
        http-client/request :chan <!
        http-client/filter-success! :body
        )))


(defn page []
  [:div.page
   [hidden-routing-state-component 
    :did-change request]
   [:h1.text-center "Sign-in: process request"]])

(def components 
  {:page page})
