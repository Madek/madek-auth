(ns madek.auth.resources.sign-in.auth-systems.main
  (:require
    [honey.sql :refer [format] :rename {format sql-format}]
    [honey.sql.helpers :as sql]
    [taoensso.timbre :refer [debug error info spy warn]]))


(defn auth-systems-query [email]
  (-> (sql/select :auth_systems))
  )

(comment
  (auth-systems-query "foo@bar.com")
  
  )

(defn handler [{{email :email} :params :as request}]
  (info request)
  (info email)
  {:body {:email email}}
  )
