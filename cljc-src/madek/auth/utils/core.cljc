(ns madek.auth.utils.core
  (:refer-clojure :exclude [str keyword]))

(defn str
  "Like clojure.core/str but maps keywords to strings without preceding colon."
  ([] "")
  ([x]
   (if (keyword? x)
     (subs (clojure.core/str x) 1)
     (clojure.core/str x)))
  ([x & yx]
   (apply clojure.core/str (concat [(str x)] (apply str yx)))))

(defn keyword
  "Like clojure.core/keyword but coerces an unknown single argument x
  with (-> x cider-ci.utils.core/str cider-ci.utils.core/keyword).
  In contrast clojure.core/keyword will return nil for anything
  not being a String, Symbol or a Keyword already (including
  java.util.UUID, Integer)."
  ([name] (cond (keyword? name) name
                :else (clojure.core/keyword (str name))))
  ([ns name] (clojure.core/keyword ns name)))

(defn deep-merge [& vals]
  (if (every? map? vals)
    (apply merge-with deep-merge vals)
    (last vals)))

(defn presence [v]
  "Returns nil if v is a blank string or if v is an empty collection.
   Returns v otherwise."
  (cond
    (string? v) (if (clojure.string/blank? v) nil v)
    (coll? v) (if (empty? v) nil v)
    :else v))

(def present? (comp boolean presence))

(defn presence! [v]
  "Pipes v through presence returns the result of that iff it is not nil.
  Throws an IllegalStateException otherwise. "
  (or (-> v presence)
      (throw
       (new
        #?(:clj IllegalStateException
           :cljs js/Error)
        "The argument must neither be nil, nor an empty string nor an empty collection."))))
