(ns madek.auth.resources.sign-in.auth-systems.auth-system.password.forgot
  (:require
   [cljs.core.async :refer [<! go go-loop]]
   [cljs.pprint :refer [pprint]]
   [lambdaisland.uri :as uri]
   [madek.auth.html.components :refer [change-username-button]]
   [madek.auth.html.forms.core :as forms]
   [madek.auth.http.client.core :as http-client]
   [madek.auth.localization :refer [translate]]
   [madek.auth.routes :refer [navigate! path]]
   [madek.auth.state :as state :refer [debug?* hidden-routing-state-component]]
   [madek.auth.utils.core :refer [presence]]
   [madek.auth.utils.json :as json]
   [reagent.core :as reagent :refer [reaction] :rename {atom ratom}]
   [taoensso.timbre :refer [debug error info spy warn]]))

(def data* (ratom {}))

(def waiting?*
  (reaction
   (->> @http-client/requests*
        (map second)
        (sort-by :timestamp)
        last
        (#(and % (-> % :response not))))))

(defn submit []
  (go (some->
        {:url (path :sign-in-user-auth-system-password-forgot
                    (:path-params @state/routing*)
                    (select-keys @data* [:email-or-login]))
         :method :post
         :modal-on-request false
         :modal-filter #(constantly true)}
       http-client/request :chan <!
       http-client/filter-success :body
       (navigate! "/my" :reload true))))

(defn page-debug []
  [:<> (when @debug?*
         [:div.debug
          [:hr]
          [:h4 "Page debug"]
          [:pre.bg-light
           [:code
            (with-out-str (pprint @data*))]]])])

(defn form []
  (let [email-or-login (get-in @state/routing* [:query-params :email-or-login])]
    [:form
     {:on-submit (fn [e] (.preventDefault e) (submit))}
     [:div
      [forms/input-component data* [:email-or-login]
       :classes "form-row" :label (translate :step2-username-label)
       :auto-focus? true
       :value email-or-login]]
     [:div.form-row
      [:button.primary-button
       {:type :submit}
       (translate :step4-forgot-password-send-label)
       (when @waiting?* "...")]]]))

(defn page []
  [:div.card-page
   [hidden-routing-state-component
    :did-mount #(swap! data* assoc :email-or-login
                       (get-in @state/routing* [:query-params :email-or-login]))]
   [:div.card-page__head [:h1 (translate :login-box-title)]]
   [:div.card-page__body {:style {:min-height "20em"}}
    [:<>
     [:h2.form-row "Receive a verification code by email to reset the password"]
     [form]]]
   [page-debug]])

(def components
  {:page page})

