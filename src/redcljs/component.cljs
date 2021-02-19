(ns redcljs.component
  (:require
   [reagent.core :as r]
   [redcljs.history :as history]))

(defn- children []
  (r/children (r/current-component)))

(defn link
  "路由组件"
  [{:keys [to]}]
  (into
   [:a
    {:on-click #(history/push to)}]
   (children)))

(defn redirect
  "路由重定向组件"
  [{:keys [from to]}]
  (history/push to)
  (fn [] [:<>]))
