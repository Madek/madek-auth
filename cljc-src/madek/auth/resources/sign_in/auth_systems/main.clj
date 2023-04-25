(ns madek.auth.resources.sign-in.auth-systems.main
  (:require
    [taoensso.timbre :refer [debug error info spy warn]]))

(defn handler [{:as request}]
  (info request)
  {:body {:foo 42}}
  )
