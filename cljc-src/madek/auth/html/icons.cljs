(ns madek.auth.html.icons
  (:refer-clojure :exclude [next])
  (:require
   ["@fortawesome/free-brands-svg-icons" :as fa-free-brands-svg-icons]
   ["@fortawesome/free-solid-svg-icons" :as fa-free-solid-svg-icons]
   ["@fortawesome/react-fontawesome" :as fa-react-fontawesome :refer [FontAwesomeIcon]]))

;(.add fa-svg-core/import
;      fa-free-solid-svg-icons/faPlusSquare
;      fa-free-solid-svg-icons/faSync)

(defn back [] (FontAwesomeIcon #js{:icon fa-free-solid-svg-icons/faArrowLeftLong :className ""}))

(defn add [] (FontAwesomeIcon #js{:icon fa-free-solid-svg-icons/faPlusSquare :className ""}))
(def create add)
(defn admin-interface [] (FontAwesomeIcon #js{:icon fa-free-solid-svg-icons/faCog :className ""}))
(defn alert [] (FontAwesomeIcon #js{:icon fa-free-solid-svg-icons/faExclamationTriangle :className ""}))
(defn inspectors [] (FontAwesomeIcon #js{:icon fa-free-solid-svg-icons/faFileImport :className ""}))
(def inspector inspectors)
(defn cancel [] (FontAwesomeIcon #js{:icon fa-free-solid-svg-icons/faTimes :className ""}))
(defn delete [] (FontAwesomeIcon #js{:icon fa-free-solid-svg-icons/faTrashAlt :className ""}))
(defn down [] (FontAwesomeIcon #js{:icon fa-free-solid-svg-icons/faCaretDown :className ""}))
(defn download [] (FontAwesomeIcon #js{:icon fa-free-solid-svg-icons/faDownload :className ""}))
(defn edit [] (FontAwesomeIcon #js{:icon fa-free-solid-svg-icons/faPen :className ""}))
(defn github [] (FontAwesomeIcon #js{:icon fa-free-brands-svg-icons/faGithubSquare :className ""}))
(defn original [] (FontAwesomeIcon #js{:icon fa-free-solid-svg-icons/faFile :className ""}))
(defn reset [] (FontAwesomeIcon #js{:icon fa-free-solid-svg-icons/faTimes :className ""}))
(defn sign-out [] (FontAwesomeIcon #js{:icon fa-free-solid-svg-icons/faArrowRightFromBracket :className ""}))

(defn save [] (FontAwesomeIcon #js{:icon fa-free-solid-svg-icons/faSave :className ""}))
(defn stop [] (FontAwesomeIcon #js{:icon fa-free-solid-svg-icons/faMinusSquare :className ""}))
(defn stores [] (FontAwesomeIcon #js{:icon fa-free-solid-svg-icons/faArchive :className ""}))
(def store stores)
(defn sync [] (FontAwesomeIcon #js{:icon fa-free-solid-svg-icons/faSync :className "" :spin true}))
(defn upload [] (FontAwesomeIcon #js{:icon fa-free-solid-svg-icons/faFileUpload :className ""}))
(defn view [] (FontAwesomeIcon #js{:icon fa-free-solid-svg-icons/faFile :className ""}))
(defn wait [& {:keys [size]
               :or {size "1x"}}]
  (FontAwesomeIcon #js{:icon fa-free-solid-svg-icons/faCircleNotch :className "" :spin true :size size}))

(defn previous []
  (FontAwesomeIcon #js{:icon fa-free-solid-svg-icons/faCaretLeft :className ""}))

(defn next []
  (FontAwesomeIcon #js{:icon fa-free-solid-svg-icons/faCaretRight :className ""}))

(defn up [] (FontAwesomeIcon #js{:icon fa-free-solid-svg-icons/faCaretSquareUp :className ""}))
