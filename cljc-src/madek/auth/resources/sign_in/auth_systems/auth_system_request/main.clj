(ns madek.auth.resources.sign-in.auth-systems.auth-system-request.main
  (:require
    [next.jdbc :as jdbc]
    [madek.auth.db.core :refer [get-ds]]
    [honey.sql :refer [format] :rename {format sql-format}]
    [honey.sql.helpers :as sql]
    [taoensso.timbre :refer [debug error info spy warn]]))



(defn handler [{{email :email
                 auth_system_id :auth_system_id} :params tx :tx :as request}]
  {:status 402})
