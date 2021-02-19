(ns redcljs.history
  (:refer-clojure :exclude [replace])
  (:require
   [reitit.core :as reitit]
   [redcljs.route :as route]
   [redcljs.global-var :as var]
   [reitit.frontend.history :as h]))

(defn- map-params-history-fn
  "参数为map类型的跳转"
  [f path & rest])

(defn- history-fn [f path & rest]
  (let [history @var/app-history
        router @var/app-router]
    (if (keyword? path)
      (apply f (into [history path] rest))
      (let [match (reitit/match-by-path router path)
            path-key (route/get-path-key (:template match))]
        (f history path-key (:path-params match) (:query-params match))))))

(def ^:private hy js/window.history)

(defn back []
  (.back hy))

(defn forward []
  (.forward hy))

(defn push [path & rest]
  (history-fn h/push-state path rest))

(defn replace [path & rest]
  (history-fn h/replace-state path rest))

(defn href [path & rest]
  (history-fn h/href path rest))
