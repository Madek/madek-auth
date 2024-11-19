(ns madek.auth.resources.sign-in.auth-systems.auth-system.password.forgot
  (:refer-clojure :exclude [get])
  (:require
   [clojure.spec.alpha :as spec]
   [clojure.string] [honey.sql :refer [format] :rename {format sql-format}]
   [honey.sql.helpers :as sql]
   [madek.auth.utils.core :refer [presence]]
   [madek.auth.resources.sign-in.auth-systems.auth-system.password.request :refer [password-auth-system!]]
   [next.jdbc :as jdbc]
   [tick.core :as tick]
   [taoensso.timbre :refer [error warn info debug spy]]))

(spec/def ::external-base-url presence)
(spec/def ::smtp_default_from_address presence)

(defn email-content [token settings]
  (clojure.string/join "\n"
                       ["To do Password reset click this link:"
                        (str (->> settings :madek_external_base_url
                                  (spec/assert ::external-base-url))
                             "/reset-password?token="
                             token)
                        ""
                        (str "Or type the token: " token)
                        ""
                        "If you did not request this, you can just ignore it."
                        "Learn more: https://docs.leihs.app/passwort-reset"]))

(def TOKEN-VALIDITY-DURATION "1 hour")

(defn insert-into-user-password-resets!
  [tx user-id email-or-login]
  (-> (sql/insert-into :user_password_resets)
      (sql/values [{:user_id [:cast user-id :uuid],
                    :used_user_param email-or-login,
                    :valid_until [:+ [:now] [:interval TOKEN-VALIDITY-DURATION]]}])
      (sql/returning :token)
      sql-format
      (->> (jdbc/execute! tx))))

(comment
  (require '[madek.auth.db.core :as db])
  (insert-into-user-password-resets! (db/get-ds)
                                     "16ae30bc-8f4a-4aef-aafe-918ec1c8b03e"
                                     "mkmit"))

(defn insert-into-emails! [tx token {user-id :id email :email} settings]
  (-> (sql/insert-into :emails)
      (sql/values [{:user_id user-id,
                    :to_address email,
                    :subject "Password reset",
                    :body (email-content token settings),
                    :from_address (->> settings
                                       :smtp_default_from_address
                                       (spec/assert ::smtp_default_from_address))}])
      sql-format
      (->> (jdbc/execute! tx))))

(defn handler [{tx :tx settings :settings
                {email-or-login :email-or-login} :params :as request}]
  (let [auth-system (password-auth-system! email-or-login tx)
        user (-> (sql/select :users.*)
                 (sql/from :users)
                 (sql/join :auth_systems_users
                           [:= :users.id :auth_systems_users.user_id])
                 (sql/where [:= :auth_systems_users.auth_system_id (:id auth-system)])
                 sql-format
                 (#(jdbc/execute-one! tx %)))]
    (let [token (-> (insert-into-user-password-resets! tx (:id user) email-or-login)
                    first
                    :token)]
      (insert-into-emails! tx token user settings))
    {:status 200, :body {:message "OK"}}))
