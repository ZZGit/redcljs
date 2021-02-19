(ns app.main
  (:require
   [reagent.core :as r]
   [re-frame.core :as rf]
   [cljs-http.client :as http]
   [redframe.core :refer [gofn reg-model]]
   [clojure.core.async :refer [chan go >! <!]]
   [redcljs.core :refer
    [start! link redirect history-push history-back href]]))

(gofn fetch-content []
      (-> (http/get
           "http://106.14.116.170:3032/app_user_member")
          (<!)
          :body))

(reg-model
 {:ns :index
  :init {:data []
         :loading false}
  :db-events {:index-set-loading
              (fn [ns [loading?]]
                (assoc ns :loading loading?))
              :index-set-data
              (fn [ns [data]]
                (assoc ns :data data))}
  :fx-events {:index-init
              {:interceptors nil
               :handler (gofn [{:keys [call]}]
                              (call :index-set-loading true)
                              (let [content (<! (fetch-content))]
                                (call :index-set-data content)
                                (call :index-set-loading false)))}}})

(defn index []
  (let [index (rf/subscribe [:index])]
    [:div
     (if (:loading @index)
       [:div "加载中..."]
       [:div "加载成功"])
     (for [d (:data @index)]
       ^{:key (:app_user_id d)}
       [:div
        (:user_realname d)])
     [:button
      {:on-click (fn []
                   (rf/dispatch [:index-init])
                   #_(rf/dispatch [:index/set-loading true 123 1123 "sdfsd" "sdfsdf"]))}
      "请求"]]))

(defn layout []
  (into
   [:div "layout"]
   (r/children
    (r/current-component))))

(defn user []
  (into
   [:div
    "user"]
   (r/children
    (r/current-component))))

(defn user-list []
  [:div "user-list"])

(defn order []
  [:div "order"])

(defn login []
  [:div "login"
   [:a
    {:on-click #(history-push "/index")}
    [:div "进入首页"]]
   [:a
    {:href (href "/index")}
    "首页"]
   [link
    {:to "/user/list"}
    [:div "用户列表页"]]
   [link
    {:to "/order/1"}
    [:div "订单页面"]]
   [link
    {:to "/sdfsdf/sdfsdf"}
    [:div "404"]]])

(defn auth []
  (into
   [:<>]
   (if true
     (r/children (r/current-component))
     [[redirect {:to "/login"}]])))

(def routes [["/" {:redirect "/index"}]
             ["/login" {:component [login]}]
             ["" {:component [layout]
                  :wrappers [auth]}
              ["/index" {:component [index]}]
              ["/user" {:component [user]}
               ["/list" {:component [user-list]}]]
              ["/order/:id" {:component [order]}]]])

(defn main! []
  (start!
   {:routes routes
    :hash? false}))
