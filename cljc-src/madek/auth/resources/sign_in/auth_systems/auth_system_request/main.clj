(ns madek.auth.resources.sign-in.auth-systems.auth-system-request.main
  (:require
    [buddy.core.keys :as keys]
    [buddy.sign.jwt :as jwt]
    [honey.sql :refer [format] :rename {format sql-format}]
    [madek.auth.resources.sign-in.auth-systems.sql :refer [auth-systems-query]]
    [honey.sql.helpers :as sql]
    [madek.auth.db.core :refer [get-ds]]
    [next.jdbc :as jdbc]
    [taoensso.timbre :refer [debug error info spy warn]]))



(defn handler [{{email :email
                 auth_system_id :auth_system_id} :params tx :tx :as request}]
  (info 'request request)

  (if-let [user-auth-system (-> email auth-systems-query spy
                                (sql/where [:= :auth_systems.id auth_system_id])
                                (sql-format :inline true)
                                spy
                                (#(jdbc/execute-one! tx %)))]
    user-auth-system ; TODO create JWT
    {:status 402}))
