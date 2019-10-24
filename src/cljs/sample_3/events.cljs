(ns sample-3.events
  (:require
    [re-frame.core :as rf]
    [ajax.core :as ajax]
    [sample-3.dev-db :as dd]
    [day8.re-frame.tracing :refer-macros [fn-traced defn-traced]]))

;;dispatchers
(rf/reg-event-db
  :navigate
  (fn-traced [db [_ route]]
    (assoc db :route route)))

;;;;;;;;;;;;;;;;;;;;;;;;;;
;; handler for :init:db ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;
(rf/reg-event-db
  :init:db
  (fn-traced [db [_]]
    (prn "init:db")
    (assoc db :data dd/total)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; adding handler for :set-result ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(rf/reg-event-db
  :set-result
  (fn-traced [db [_ output]]
    (prn "set-result")
    (assoc db :data output)))

(rf/reg-event-db
  :set-docs
  (fn-traced [db [_ docs]]
    (assoc db :docs docs)))

(rf/reg-event-db
  :set-docs
  (fn-traced [db [_ docs]]
    (assoc db :docs docs)))

;(rf/reg-event-fx
;  :fetch-result
;  (fn-traced [_ _]
;    {:http-xhrio {:method          :get
;                  :uri             "/result"
;                  :response-format (ajax/raw-response-format)
;                  :on-success       []}}))

(rf/reg-event-db
  :common/set-error
  (fn-traced [db [_ error]]
    (assoc db :common/error error)))

;;subscriptions

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; adding handler for :result ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(rf/reg-sub
  :output
  (fn [db _]
    (-> db :data)))

(rf/reg-sub
  :route
  (fn [db _]
    (-> db :route)))

(rf/reg-sub
  :page
  :<- [:route]
  (fn [route _]
    (-> route :data :name)))

(rf/reg-sub
  :docs
  (fn [db _]
    (:docs db)))

(rf/reg-sub
  :common/error
  (fn [db _]
    (:common/error db)))
