(ns sample-3.core
  (:require
    [day8.re-frame.http-fx]
    [reagent.core :as r]
    [re-frame.core :as rf]
    [goog.events :as events]
    [goog.history.EventType :as HistoryEventType]
    [markdown.core :refer [md->html]]
    [sample-3.ajax :as ajax]
    [sample-3.events]
    [reitit.core :as reitit]
    [clojure.string :as string]
    [ajax.core :refer [GET POST]])
  (:import goog.History))

(defn nav-link [uri title page]
  [:a.navbar-item
   {:href   uri
    :class (when (= page @(rf/subscribe [:page])) :is-active)}
   title])

(defn navbar []
  (r/with-let [expanded? (r/atom false)]
    [:nav.navbar.is-info>div.container
     [:div.navbar-brand
      [:a.navbar-item {:href "/" :style {:font-weight :bold}} "sample-3"]
      [:span.navbar-burger.burger
       {:data-target :nav-menu
        :on-click #(swap! expanded? not)
        :class (when @expanded? :is-active)}
       [:span][:span][:span]]]
     [:div#nav-menu.navbar-menu
      {:class (when @expanded? :is-active)}
      [:div.navbar-start
       [nav-link "#/" "Home" :home]]]]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; using dispatch instead of dispatch-sync ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;(defn submit [total]
;  (rf/dispatch [:set-result total]))
(defn submit []
  (rf/dispatch [:compute]))

;(defn add [x y]
;  (prn "add function")
;
;  (POST "/api/math/plus"
;        {:headers {"Accept" "application/transit+json"}
;         :params {:x x :y y}
;         :handler #(submit (:total %))}))

(defn set-x [x]
  (rf/dispatch-sync [:set-x x]))

(defn set-y [y]
  (rf/dispatch-sync [:set-y y]))

(defn set-operator [op]
  (rf/dispatch-sync [:set-op op]))

(defn input-field [tag id data]
  [:div.field
   [tag
    {:type :number
     :value data
     :on-change #(do
                   (cond
                     (= id :input-1) (set-x (js/parseInt (-> % .-target .-value)))
                     (= id :input-2) (set-y (js/parseInt (-> % .-target .-value))))
                   ;(add @(rf/subscribe [:input-1]) @(rf/subscribe [:input-2])))}]])
                   (submit))}]])


(defn- make-row [x y]
  [:tr
   [:td [input-field :input.input :input-1 x]]
   [:td [:select {
                  :on-change #(set-operator (-> % .-target .-value))}
         [:option "+"]
         [:option "-"]
         [:option "*"]
         [:option "/"]]]
   [:td [input-field :input.input :input-2 y]]
   [:td "="]
   [:td @(rf/subscribe [:output])]])

(defn home-page []

  (let [output (rf/subscribe [:output])
        x (rf/subscribe [:input-1])
        y (rf/subscribe [:input-2])]

    (fn []
      [:section.section>div.container>div.content]
      [:div.columns>div.column.is-one-third>div.column
       [:p "home-page"]
       [:p "2 + 3 = " @output]

       [:table
        [:tbody
         (make-row @x @y)]]])))

(def pages
  {:home #'home-page})

(defn page []
  [:div
   [navbar]
   [(pages @(rf/subscribe [:page]))]])

;; -------------------------
;; Routes

(def router
  (reitit/router
    [["/" :home]]))

;; -------------------------
;; History
;; must be called after routes have been defined
(defn hook-browser-navigation! []
  (doto (History.)
    (events/listen
      HistoryEventType/NAVIGATE
      (fn [event]
        (let [uri (or (not-empty (string/replace (.-token event) #"^.*#" "")) "/")]
          (rf/dispatch
            [:navigate (reitit/match-by-path router uri)]))))
    (.setEnabled true)))

;; -------------------------
;; Initialize app
(defn mount-components []
  (rf/clear-subscription-cache!)
  (r/render [#'page] (.getElementById js/document "app")))

(defn init! []
  (rf/dispatch-sync [:navigate (reitit/match-by-name router :home)])
  (ajax/load-interceptors!)

  (rf/dispatch-sync [:init:db])

  (hook-browser-navigation!)
  (mount-components))
