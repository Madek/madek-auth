(ns madek.auth.resources.sign-in.auth-systems.auth-system.password.reset
  (:refer-clojure :exclude [get])
  (:require
   [clojure.spec.alpha :as spec]
   [clojure.string :as string]
   [honey.sql :refer [format] :rename {format sql-format}]
   [honey.sql.helpers :as sql]
   [madek.auth.utils.core :refer [presence]]
   [madek.auth.resources.sign-in.auth-systems.auth-system.password.request :refer [password-auth-system!]]
   [next.jdbc :as jdbc]
   [tick.core :as tick]
   [taoensso.timbre :refer [error warn info debug spy]]
   [tick.core :as tick]))

(defn normalize-token-str [str]
  (-> str
      string/upper-case
      (string/escape {\O 0, \I 1, \L 1})))

(defn get-from-user-password-resets
  [tx token-param]
  (-> (sql/select :*)
      (sql/from :user_password_resets)
      (sql/where [:= (normalize-token-str token-param) :token])
      sql-format
      (->> (jdbc/execute-one! tx))))

(defn get [{tx :tx settings :settings
            {token :token} :params :as request}]
  (let [pwd-reset (get-from-user-password-resets tx token)]
    (cond
      (not pwd-reset)
      {:status 404,
       :body {:error_message "Password reset for the token not found."}}

      (tick/> (tick/now) (:valid_until pwd-reset))
      {:status 403,
       :body {:error_message "The token has expired."}}

      :else {:status :201, :body "OK"})))

(defn post [{tx :tx settings :settings
             {token :token password :password} :params :as request}]
  )

(defn dispatch [request]
  (case (:request-method request)
    :get (get request)
    :post (post request)
    (throw (ex-info "Unknown method" {}))))
