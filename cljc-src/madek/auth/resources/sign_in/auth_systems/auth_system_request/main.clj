(ns madek.auth.resources.sign-in.auth-systems.auth-system-request.main
  (:require
    [buddy.core.keys :as keys]
    [buddy.sign.jwt :as jwt]
    [cuerdas.core :as str]
    [honey.sql :refer [format] :rename {format sql-format}]
    [honey.sql.helpers :as sql]
    [madek.auth.db.core :refer [get-ds]]
    [madek.auth.resources.sign-in.auth-systems.sql :refer [auth-systems-query]]
    [madek.auth.routes :refer [path]]
    [madek.auth.utils.core :refer [presence]]
    [next.jdbc :as jdbc]
    [tick.core :as time]
    [taoensso.timbre :refer [debug error info spy warn]]))


(defn query [email auth_system_id]
  (-> email auth-systems-query 
      spy
      (sql/select-distinct [:auth_systems.internal_private_key :internal_private_key])
      (sql/where [:= :auth_systems.id auth_system_id])))


(defn prepare-key-str [s]
  (->> (-> s (str/split #"\n"))
       (map str/trim)
       (map presence)
       (filter identity)
       (str/join "\n")))

(defn private-key! [s]
  (-> s prepare-key-str keys/str->private-key
      (or (throw
            (ex-info "Private key error!"
                     {:status 500})))))

(defn public-key! [s]
  (-> s prepare-key-str keys/str->public-key
      (or (throw
            (ex-info "Public key error!"
                     {:status 500})))))

(defn claims! [user-auth-system return-to]
  (-> user-auth-system
      (select-keys [:email :login])
      (merge 
        {:exp (time/>> (time/now) (time/of-seconds 90))
         :iat (time/now)
         :return_to (presence return-to)
         :path (path :ext-auth-sign-in 
                     (select-keys user-auth-system [:auth_system_id])
                     )})))

(defn handler [{{email :email
                 auth_system_id :auth_system_id
                 return-to :return-to} :params tx 
                :tx :as request}]
  (info 'request request)
  (if-let [user-auth-system (-> (query email auth_system_id)                
                                (sql-format :inline true)
                                spy
                                (#(jdbc/execute-one! tx %))
                                spy
                                )]
    (let [priv-key (-> user-auth-system :internal_private_key private-key!)
          claims (claims! user-auth-system return-to)
          token (jwt/sign claims priv-key {:alg :es256})]
      {:body {:token token}}) ; TODO create JWT
    {:status 402}))
