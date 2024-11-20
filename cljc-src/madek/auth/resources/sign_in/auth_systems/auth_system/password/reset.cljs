(ns madek.auth.resources.sign-in.auth-systems.auth-system.password.reset
  (:require
   [cljs.core.async :refer [<! go]]
   [cljs.pprint :refer [pprint]]
   [madek.auth.html.forms.core :as forms]
   [madek.auth.http.client.core :as http-client]
   [madek.auth.localization :refer [translate]]
   [madek.auth.routes :refer [navigate! path]]
   [madek.auth.state :as state :refer [debug?* hidden-routing-state-component]]
   [madek.auth.utils.core :refer [presence]]
   [reagent.core :as reagent :refer [reaction] :rename {atom ratom}]
   [taoensso.timbre :refer [debug info warn]]))

(def data* (ratom {}))
(def response-data* (ratom {}))

(def waiting?*
  (reaction
   (->> @http-client/requests*
        (map second)
        (sort-by :timestamp)
        last
        (#(and % (-> % :response not))))))

(defn handle-validate-token-response [response]
  (warn response)
  (reset! response-data* response))

(defn validate-token [& _]
  (when-let [token (-> @state/routing* :query-params :token presence)]
    (swap! data* assoc :token token)
    (go (some->> {} http-client/request :chan <!
                 handle-validate-token-response))))

(defn handle [response]
  (warn response)
  (reset! response-data* response)
  (when (< (:status response) 300)
    (navigate! (path :sign-in-user-auth-system-password-forgot
                     (:path-params @state/routing*)
                     (some-> @state/state*
                             :routing
                             :query-params
                             (select-keys [:return-to :lang])))
               :reload true)))

(defn submit []
  (info "submit")
  #_(go (some->
          {:url (path :sign-in-user-auth-system-password-forgot
                      (:path-params @state/routing*)
                      (select-keys @data* [:email-or-login]))
           :method :post
           :modal-on-request false
           :modal-on-response-error true}
          http-client/request :chan <!
          handle)))

(defn page-debug []
  [:<> (when @debug?*
         [:div.debug
          [:hr]
          [:h4 "Page debug"]
          [:pre.bg-light
           [:code
            (with-out-str (pprint @data*))]]])])

(defn form []
  [:form
   {:on-submit (fn [e] (.preventDefault e)
                 ((if (:email-or-login @data*) submit validate-token)))}
   (when (some-> @response-data* :status (#(> % 300)))
     [:div.form-row
      [:div.validation-message
       (if-let [msg (some->> @response-data* :body :error_message)] msg "Error")]])
   [:div.form-row
    [forms/input-component data* [:token]
     :classes "form-row" :label (translate :step5-reset-password-input-label)
     :auto-focus? true]]
   [:div.form-row
    [:button.primary-button {:type :submit}
     (translate :step1-submit-label) (when @waiting?* "...")]]])

(defn page []
  [:div.card-page
   [hidden-routing-state-component :did-mount validate-token]
   [:div.card-page__head [:h1 (translate :login-box-title)]]
   [:div.card-page__body {:style {:min-height "20em"}}
    (let [email-or-login (get-in @state/routing* [:query-params :email-or-login])]
      [:<>
       [:h2.form-row (translate :step5-reset-password-txt)]
       [form]])]
   [page-debug]])

(def components
  {:page page})
