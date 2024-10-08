(ns demo.macros
  (:require [clojure.string :as str]))

(def tag-pattern
  (re-pattern
    (str "([a-z][a-z0-9-]*)"
         "(#[a-z][a-z0-9-]*)?"
         "((\\.[a-z][a-z0-9-]*)*)")))

(defn- parse-tag [s]
  (let [[_ tag id class :as matches] (re-matches tag-pattern s)]
    (prn matches)
    (if-not tag
      (throw (ex-info "Parse error" {:s s :matches matches}))
      (merge {:tag tag}
             (when (seq id) {:id (subs id 1)})
             (when (seq class) {:class (str/replace (subs class 1) "." " ")})))))

(defn html* [form]
  (let [{:keys [tag] :as parsed} (parse-tag (name (first form)))
        attrs (merge (select-keys parsed [:id :class])
                     (when (map? (second form)) (second form)))
        children (if (map? (second form)) (drop 2 form) (rest form))
        lit-attrs# (if attrs
                     (into {} (map (fn [[k v]] [(name k) v])) attrs)
                     {})]
    `("<" ~tag
          ~@(->> lit-attrs#
                 (mapcat (fn [[k v]]
                           (let [k' (name k)
                                 v' (if (keyword? v) (name v) v)]
                             [" " k' "=\"" v' "\""]))))
          ">"
          ~@(mapcat #(if (vector? %) (html* %) [%]) children)
          "</" ~tag ">")))

(defmacro html [form]
  `(~'shadow.cljs.modern/js-template ~'lit/html ~@(html* form)))
