(ns madek.auth.resources.sign-in.auth-systems.main
  (:require
    [cljs.core.async :refer [go go-loop]]
    [cljs.pprint :refer [pprint]]
    [madek.auth.html.forms.core :as forms]
    [madek.auth.html.icons :as icons]
    [madek.auth.http.client.core :as http-client]
    [madek.auth.routes :refer [navigate! path]]
    [madek.auth.state :as state :refer [debug?* hidden-routing-state-component]]
    [madek.auth.utils.core :refer [presence]]
    [reagent.core :as reagent :rename {atom ratom}]
    [taoensso.timbre :refer [debug error info spy warn]]))


(defonce data* (ratom nil))

(defn request-auth-systems [& _]
  (reset! data* nil)
  (go (some->>
        {} http-client/request :chan <!
        http-client/filter-success! :body
        (reset! data*))))

(defn page-debug []
  [:<> (when @debug?*
         [:div.debug
          [:hr]
          [:h4 "Page debug"]
          [:pre.bg-light
           [:code
            (with-out-str (pprint @data*))]]])])

(defn auth-systems []
  [:div 
   (for [sys @data*]
     ^{:key (:auth_system_id sys)} 
     [:div  (:auth_system_name sys)]) ])

(defn page []
  [:div
   [hidden-routing-state-component
    :did-change request-auth-systems]
   [:h1.text-center "Sign-in: choose an authentication method"]
   (cond 
     (nil? @data*)  [:div [:h2 "Wait"]]
     (empty? @data*) [:div [:h2 "Oh No No"]]
     :else [:div [auth-systems]])
   [:div.d-flex.mb-3
    [:a.btn.btn-warning {:href (path :sign-in {} (some-> @state/state* :routing 
                                                         :query-params (select-keys [:redirect-to])) )}
     [:span [icons/back] " Use a different e-mail address"]] ]

   [page-debug]])

(def components 
  {:page page})
