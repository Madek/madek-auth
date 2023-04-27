(ns madek.auth.resources.sign-in.main
  (:require
    [cljs.pprint :refer [pprint]]
    [cljs.core.async :refer [go go-loop]]
    [madek.auth.html.forms.core :as forms]
    [madek.auth.http.client.core :as http-client]
    [madek.auth.routes :refer [navigate! path]]
    [madek.auth.state :as state :refer [debug?*]]
    [madek.auth.utils.core :refer [presence]]
    [reagent.core :as reagent :rename {atom ratom}]
    [taoensso.timbre :refer [debug error info spy warn]]))


(defonce data* (ratom {}))

(defn request-auth-systems []
  (info 'request)
  (go (-> 
        {:url (path :sign-in-auth-systems {} (select-keys @data*  [:email]))}
        http-client/request :chan <!
        http-client/filter-success! :body)))

