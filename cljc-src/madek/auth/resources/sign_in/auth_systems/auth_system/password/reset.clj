(ns madek.auth.resources.sign-in.auth-systems.auth-system.password.reset
  (:refer-clojure :exclude [get])
  (:require
   [clojure.spec.alpha :as spec]
   [clojure.string :as string]
   [honey.sql :refer [format] :rename {format sql-format}]
   [honey.sql.helpers :as sql]
   [madek.auth.resources.sign-in.auth-systems.auth-system.password.request :refer [password-auth-system!]]
   [madek.auth.utils.core :refer [presence]]
   [next.jdbc :as jdbc]
   [taoensso.timbre :refer [error warn info debug spy]]
   [tick.core :as tick]
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

(defn get [{tx :tx {token :token} :params :as request}]
  (let [pwd-reset (get-from-user-password-resets tx token)]
    (cond
      (not pwd-reset)
      {:status 404,
       :body {:error_message "Password reset for the token not found."}}

      (tick/> (tick/now) (:valid_until pwd-reset))
      {:status 403,
       :body {:error_message "The token has expired."}}

      :else {:status 201,
             :body {:email-or-login (:used_user_param pwd-reset)}})))

; ==================================================================================================

(defn password-hash [tx password]
  (-> (sql/select [[:crypt password [:gen_salt "bf"]] :pw_hash])
      sql-format
      (->> (jdbc/execute-one! tx))
      :pw_hash))

(defn sql-command [user-id pw-hash]
  (-> (sql/insert-into :auth_systems_users)
      (sql/values [{:user_id user-id
                    :auth_system_id "password"
                    :data pw-hash}])
      ; (sql/on-conflict :user_id :authentication_system_id)
      ; (sql/do-update-set :data {:raw "EXCLUDED.data"})
      (sql/returning :*)
      (sql-format :inline true)))

(defn set-password [tx user-id password]
  (let [pw-hash (password-hash password tx)
        sql-command (sql-command user-id pw-hash)
        #_#_result (jdbc/execute! tx sql-command {:return-keys true})]
    sql-command
    #_{:body result}))

(comment (do (require '[madek.auth.db.core :as db])
             ; (password-hash (db/get-ds) "password")
             (sql-command #uuid "16ae30bc-8f4a-4aef-aafe-918ec1c8b03e"
                          "password")
             ; (set-password (db/get-ds)
             ;               #uuid "16ae30bc-8f4a-4aef-aafe-918ec1c8b03e"
             ;               "password")
             ))

(defn post
  [{tx :tx {token :token password :password} :body :as request}]
  (let [pwd-reset (get-from-user-password-resets tx token)]
    (cond
      (not pwd-reset)
      {:status 404,
       :body {:error_message "Password reset for the token not found."}}

      (tick/> (tick/now) (:valid_until pwd-reset))
      {:status 403,
       :body {:error_message "The token has expired."}}

      :else (do #_(set-password (:user_id user) password tx)
             {:status 200 :body "OK"}))))

; ==================================================================================================

(defn dispatch [request]
  (case (:request-method request)
    :get (get request)
    :post (post request)
    (throw (ex-info "Unknown method" {}))))
