(ns redcljs.route
  (:require [reitit.core :as reitit]))

(defn- get-wrappers-prop
  "获取路由wrappers属性"
  [route]
  (-> route :data :wrappers))

(defn- get-component-prop
  "获取路由component属性"
  [route]
  (-> route :data :component))

(defn get-redirect-prop
  "获取路由redirect属性"
  [route]
  (-> route :data :redirect))

(defn- bind-components [components]
  (reduce (fn [c p] [p c]) nil components))

(defn- get-component-of-wrappers
  "获取wrappers属性的组件"
  [wrappers components]
  (bind-components (concat components wrappers)))

(defn- get-component-of-component
  "获取component属性的组件"
  [components]
  (let [component-count (count components)]
    (cond
      (zero? component-count) (prn "error")
      (= component-count 1) [(first components)]
      :else (bind-components components))))

(defn get-component-by-route
  "通过路由信息获取组件"
  [route]
  (let [wrappers (get-wrappers-prop route)
        components (reverse (get-component-prop route))]
    (if (seq wrappers)
      (get-component-of-wrappers wrappers components)
      (get-component-of-component components))))

(defn get-path-key [path]
  (->> (clojure.string/trim path)
       (hash)
       (str "PATH-KEY")
       (keyword)))

(defn- add-route-name [[path opt]]
  (let [name (or (:name opt) (get-path-key path))]
    [path (assoc opt :name name)]))

(defn to-reitit-router [router]
  (let [rs (reitit/routes router)]
    (reitit/router
     (map add-route-name rs))))


