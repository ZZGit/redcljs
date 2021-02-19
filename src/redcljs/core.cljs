(ns redcljs.core
  (:require
   [re-frame.core :as rf]
   [reagent.dom :as dom]
   [reitit.core :as reitit]
   [reitit.frontend.easy :as rfe]
   [reitit.frontend.controllers :as rfc]
   [redcljs.route :as route]
   [redcljs.global-var :as var]
   [redcljs.history :as history]
   [redcljs.component :as component]))

;;;; History
(def history-back history/back)
(def history-forward history/forward)
(def history-push history/push)
(def history-replace history/replace)
(def href history/href)

;;;; Component
(def link component/link)
(def redirect component/redirect)

(defn- root-component []
  (let [route (rf/subscribe [:redcljs/route])
        redirect-url (route/get-redirect-prop @route)]
    (if (seq redirect-url)
      [redirect {:to redirect-url}]
      (route/get-component-by-route @route))))

(defn- reg-route-event&sub []
  (rf/reg-event-db
   :redcljs/change-route
   (fn [db [_ route]]
     (assoc db :redcljs/route route)))
  (rf/reg-sub
   :redcljs/route
   (fn [db] (:redcljs/route db nil))))

;;;默认配置
(def ^:private default-config
  {:root-id "app"
   :routes []
   :hash? false})

(defn start!
  [config]
  (let [opt (merge default-config config)
        router (reitit/router (:routes opt))]
    (reset! var/app-router router)
    (reg-route-event&sub)
    (rfe/start!
     (route/to-reitit-router router)
     (fn [new-match history]
       (when-not @var/app-history (reset! var/app-history history))
       (rf/dispatch [:redcljs/change-route new-match]))
     {:use-fragment (:hash? opt)})
    (dom/render
     [root-component]
     (.getElementById js/document (:root-id opt)))))
