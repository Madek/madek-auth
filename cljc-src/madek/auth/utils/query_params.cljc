(ns madek.auth.utils.query-params
  (:require
   #?(:clj [ring.util.codec])
   [clojure.string :as string]
   [clojure.walk :refer [keywordize-keys]]
   [madek.auth.utils.json :refer [to-json from-json try-parse-json]]
   [madek.auth.utils.url :as url]))

(defn decode [query-string & {:keys [parse-json?]
                              :or {parse-json? false}}]
  (let [parser (if parse-json? try-parse-json identity)]
    (->> (if (string/blank? query-string)
           []
           (string/split query-string #"&"))
         (reduce
          (fn [m part]
            (let [[k v] (string/split part #"=" 2)]
              (assoc m (-> k url/decode keyword)
                     (-> v url/decode parser))))
          {})
         keywordize-keys)))

(defn encode [params]
  (->> params
       (map (fn [[k v]]
              (str (-> k str url/encode)
                   "="
                   (-> (if (coll? v) (to-json v) v)
                       str url/encode))))
       (clojure.string/join "&")))
