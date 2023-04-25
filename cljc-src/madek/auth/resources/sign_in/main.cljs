(ns madek.auth.resources.sign-in.main
  (:require
    [cljs.pprint :refer [pprint]]
    [madek.auth.html.forms.core :as forms]
    [madek.auth.routes :refer [navigate! path]]
    [madek.auth.state :as state :refer [debug?*]]
    [madek.auth.utils.core :refer [presence]]
    [reagent.core :as reagent :rename {atom ratom}]
    [taoensso.timbre :refer [debug error info spy warn]]))



(defonce data* (ratom {}))


(defn email-form []
  [:div.row
   [:div.col-md-3]
   [:div.col-md
    [:form.form
     {:on-submit (fn [e]
                   (.preventDefault e)
                   (info :on-submit)
                   (navigate! (path :sign-in {} 
                                    {:email (get-in @data* [:email])})
                              e :reload true))}
     [:div.mb-3
      [:label.col-form-label {:for :email}  
       "Supply your " [:b "email address "] " to sign in" ]
      [:input.form-control 
       {:id :email
        :value (get-in @data* [:email])
        :on-change #(-> % .-target .-value presence (forms/set-value data* [:email]))
        }
       ]]
     [:div.d-flex.mb-3
      [:div.ms-auto
       [:button.btn.btn-primary {:type :submit} "Continue"]]]

     ]]
   [:div.col-md-3]])

(defn page-debug []
       [:<> (when @debug?*
              [:div.debug
               [:hr]
               [:h4 "Page debug"]
               [:pre.bg-light
                [:code
                 (with-out-str (pprint @data*))
                 ]]
               ])])

(defn page []
  [:div
   [email-form]
   [page-debug]])


(def components 
  {:page page})
