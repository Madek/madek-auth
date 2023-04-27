(ns madek.auth.routing.main
  (:require
    [clojure.pprint :refer [pprint]]
    [clojure.walk :refer [keywordize-keys]]
    [cuerdas.core :as str]
    [madek.auth.html.history-navigation :as navigation]
    [madek.auth.routes :as routes :refer [path]]
    [madek.auth.routing.resolve :refer [routes-resources]]
    [madek.auth.state :as state]
    [madek.auth.utils.url :as url]
    [madek.auth.utils.yaml :as yaml]
    [reagent.core :as reagent]
    [reitit.core :as reitit]
    [taoensso.timbre :refer [debug info warn error spy]]
    ))


(defn on-navigate [url match]
  (let [name (get-in match[:data :name])
        component (get routes-resources name)]
    (as-> match state
      (assoc state :name name)
      (assoc state :page (-> component :page))
      (assoc state :center-nav (-> component :center-nav))
      (assoc state :page-nav (-> component :page-nav))
      (assoc state :query (some->> url :query))
      (assoc state :query-params (some-> url :query 
                                         (str/split "&")
                                         (some->> (map #(str/split % "=" 2))
                                                  (into {})
                                                  keywordize-keys)))
;     (assoc state :query-params-parsed (some->> state :query-params (map (fn [[k v]] [k (yaml/parse v)])) (into {})))
      (assoc state :route (path (:name state)
                                (:path-params state)
                                (:query-params state)))
      (reset! state/routing* state))))

(defn navigate? [url]
  (debug 'navigate? url)
  (when-let [match (spy (reitit/match-by-path routes/router (:path url)))]
    (debug {:url url :match match})
    (when (not (or (get-in match [:data :bypass-spa])
                   (get-in match [:data :external])))
      match)))

(defn init-navigation []
  (navigation/init! on-navigate :navigate? navigate?))

(defn init-query-param-debug-state []
  (swap! state/debug?*
         (fn [v] (or v (boolean (get-in @state/routing* [:query-params-parsed :debug]))))))

(defn init []
  (info "initializing routing ...")
  (init-navigation)
  (init-query-param-debug-state)
  (when (-> @state/server* :needs_init)
    (navigation/navigate! (path :init)))
  (info "initialized routing " @state/routing*))
