(ns app.macros)

(defmacro defasync [f args & rest]
  `(defn ~f ~args
     (let [r# (chan)]
       (go
         (>! r# (do ~@rest)))
       r#)))
