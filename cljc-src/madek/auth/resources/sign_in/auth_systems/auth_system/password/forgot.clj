(ns madek.auth.resources.sign-in.auth-systems.auth-system.password.forgot
  (:refer-clojure :exclude [get])
  (:require
   [clojure.spec.alpha :as spec]
   [clojure.string]
   [honey.sql :refer [format] :rename {format sql-format}]
   [honey.sql.helpers :as sql]
   [madek.auth.routes :refer [path]]
   [next.jdbc :as jdbc]))

(defn email-content [token settings]
  (let [token-path (path :sign-in-user-auth-system-password-reset
                         {:auth_system_type "password"
                          :auth_system_id "password"}
                         {:token token})]
    (str "Hello,\n"
         "\n"
         "You have requested to reset your password for your account on media archive.\n"
         "\n"
         "To reset your password click on this link:\n"
         (str (:madek_external_base_url settings) token-path "\n")
         "\n"
         "Enter the following code on the website in case the link does not work: " token "\n"
         "\n"
         "If you did not request this, you can just ignore it.")))

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
                    :subject "Media archive: password reset",
                    :body (email-content token settings),
                    :from_address (->> settings
                                       :smtp_default_from_address
                                       (spec/assert ::smtp_default_from_address))}])
      sql-format
      (->> (jdbc/execute! tx))))

(defn handler [{tx :tx settings :settings
                {email-or-login :email-or-login} :params :as request}]
  (if-let [user (-> (sql/select :users.*)
                    (sql/from :users)
                    (sql/where [:= :users.password_sign_in_enabled true])
                    (sql/where [:or [:= :users.email email-or-login]
                                [:= :users.login email-or-login]])
                    sql-format
                    (#(jdbc/execute-one! tx %)))]
    (let [token (-> (insert-into-user-password-resets! tx (:id user) email-or-login)
                    first
                    :token)]
      (insert-into-emails! tx token user settings)
      {:status 200, :body {:message "OK"}})
    {:status 403, :body {:error_message "User not found or password sign-in for the user is disabled."}}))
