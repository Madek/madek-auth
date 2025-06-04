(ns madek.auth.resources.sign-out.main
  (:require
   [honey.sql :refer [format] :rename {format sql-format}]
   [honey.sql.helpers :as sql]
   [madek.auth.constants :refer [MADEK_RAILS_SESSION_COOKIE_NAME
                                 MADEK_SESSION_COOKIE_NAME]]
   [next.jdbc :as jdbc]
   [taoensso.timbre :refer [debug error info spy warn]]))

(defn handler [{tx :tx :as request}]
  (if-let [session-id (some-> request :authenticated-entity :session_id)]
    (if (-> (sql/delete-from :user_sessions)
            (sql/where [:= :id session-id])
            (sql-format)
            (#(jdbc/execute-one! tx % {:return-keys true})))
      (merge
       {:cookies {MADEK_SESSION_COOKIE_NAME {:path "/" :value "" :max-age 0}
                  MADEK_RAILS_SESSION_COOKIE_NAME {:path "/" :value "" :max-age 0}}}
       (if (= :html (-> request :accept :mime))
         {:status 302
          :headers {"Location" "/"}}
         {:status 204}))
      (throw (ex-info "Session was not deleted" {})))
    {:status 401}))
