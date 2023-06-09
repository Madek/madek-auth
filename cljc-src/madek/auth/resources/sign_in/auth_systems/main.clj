(ns madek.auth.resources.sign-in.auth-systems.main
  (:require
    [honey.sql :refer [format] :rename {format sql-format}]
    [honey.sql.helpers :as sql]
    [madek.auth.resources.sign-in.auth-systems.sql :refer [auth-systems-query]]
    [next.jdbc :as jdbc]
    [taoensso.timbre :refer [debug error info spy warn]]))

(defn auth-systems [email tx]
  (-> email
      auth-systems-query
      sql-format
      (#(jdbc/execute! tx %))))

(defn handler [{{email :email} :params tx :tx :as request}]
  (info request)
  (info email)
  {:body (auth-systems email tx)})
