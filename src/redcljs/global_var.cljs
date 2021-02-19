(ns redcljs.global-var
  (:require
   [reagent.core :as r]))

;;;; 全局变量

(defonce app-router (r/atom nil))

(defonce app-history (r/atom nil))
