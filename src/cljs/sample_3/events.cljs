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
    (assoc db :data { :total dd/total
                      :x dd/x
                      :y dd/y
                      :op dd/op})))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; adding handler for :set-result ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(rf/reg-event-db
  :set-result
  (fn-traced [db [_ output]]
    (prn "set-result")
    (assoc-in db [:data :total] output)))

(rf/reg-event-db
  :set-x
  (fn-traced [db [_ x]]
             (prn "set-x")
             (assoc-in db [:data :x] x)))

(rf/reg-event-db
  :set-y
  (fn-traced [db [_ y]]
             (prn "set-y")
             (assoc-in db [:data :y] y)))

(rf/reg-event-db
  :set-op
  (fn-traced [db [_ op]]
             (prn "set-op")
             (assoc-in db [:data :op] op)))

(rf/reg-event-db
  :compute
  (fn-traced [db _]
             (prn "compute")
             ;(:set-result (+ (-> db :data :x) (-> db :data :y)))))
             (assoc-in db [:data :total] (+ (-> db :data :x) (-> db :data :y)))))

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
    (-> db :data :total)))

(rf/reg-sub
  :input-1
  (fn [db _]
    (-> db :data :x)))

(rf/reg-sub
  :input-2
  (fn [db _]
    (-> db :data :y)))

(rf/reg-sub
  :operation
  (fn [db _]
    (-> db :data :op)))

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
