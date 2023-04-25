(ns madek.auth.http.anti-csrf.main
  (:require
    [madek.auth.http.shared :refer [ANTI_CRSF_TOKEN_COOKIE_NAME]]
    [goog.net.Cookies]
    ))

(defn token []
  (.get goog.net.Cookies ANTI_CRSF_TOKEN_COOKIE_NAME))

(defn hidden-form-group-token-component []
  [:div.form-group
   [:input
    {:name :csrf-token
     :type :hidden
     :value (token)}]])
