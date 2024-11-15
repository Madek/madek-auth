(ns madek.auth.resources.sign-in.auth-systems.auth-system.password.forgot
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

(def BASE32_CROCKFORD "0123456789ABCDEFGHJKMNPQRSTVWXYZ")

(defn base32-crockford-rand-char []
  (rand-nth BASE32_CROCKFORD))

(defn base32-crockford-rand-str
  ([] (base32-crockford-rand-str 10))
  ([n]
   (->> (repeatedly base32-crockford-rand-char)
        (take n)
        (apply str))))

(comment
  (base32-crockford-rand-char)
  (base32-crockford-rand-str))

(defn email-content
  [token {settings :settings}]
  (clojure.string/join "\n"
                       ["To do Password reset click this link:"
                        (str (->> settings :external_base_url (spec/assert ::external-base-url))
                             "/reset-password?token="
                             token)
                        ""
                        (str "Or type the token: " token)
                        ""
                        "If you did not request this, you can just ignore it."
                        "Learn more: https://docs.leihs.app/passwort-reset"]))

(defn make-token [n] (base32-crockford-rand-str n))

(defn normalize-token-str
  [str]
  (clojure.string/escape (clojure.string/upper-case str) {\O 0, \I 1, \L 1}))

(defn handler [{{email-or-login :email-or-login} :params tx :tx :as request}]
  (debug request)
  (debug email-or-login)
  (password-auth-system! email-or-login tx)
  {:status 200, :body {:message "OK"}})
