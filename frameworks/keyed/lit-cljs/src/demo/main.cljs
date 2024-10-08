(ns demo.main
  (:require [shadow.cljs.modern :refer [js-template]]
            ["lit-html" :as lit]
            [demo.util :as u]
            ["lit/directives/repeat.js" :as lit-repeat])
  (:require-macros [demo.macros :refer [html]]))

(defonce id-atom (atom 0))
(defonce data (atom []))
(defonce selected (atom nil))

(defn row [data on-click on-delete]
  (let [selected? (= @selected (:id data))]
    (html
      [:tr
       {:class (when selected? "danger")}
       [:td.col-md-1 (:id data)]
       [:td.col-md-4
        [:a {"@click" (fn [e] (on-click (:id data)))}
         (:label data)]]
       [:td.col-md-1
        [:a {"@click" (fn [e] (on-delete (:id data)))}
         [:span.glyphicon.glyphicon-remove
          {:aria-hidden "true"}]]]
       [:td.col-md-6]])))

(defn run [_]
  (reset! data (vec (u/build-data id-atom 1000)))
  (reset! selected nil))

(defn run-lots [_]
  (reset! data (vec (u/build-data id-atom 10000)))
  (reset! selected nil))

(defn add [_]
  (swap! data u/add id-atom))

(defn update-some []
  (swap! data u/update-some))

(defn clear []
  (reset! selected nil)
  (reset! data []))

(defn swap-rows []
  (swap! data u/swap-rows))

(defn select [id]
  (reset! selected id))

(defn delete [id]
  (swap! data u/delete-row id))

(defn app []
  (html
    [:div.container
     [:div.jumbotron
      [:div.row
       [:div.col-md-6
        [:h1 "lit-cljs"]]
       [:div.col-md-6
        [:div.row
         [:div.col-sm-6.smallpad
          [:button.btn.btn-primary.btn-block
           {:type "button"
            :id "run"
            "@click" run}
           "Create 1,000 rows"]]
         [:div.col-sm-6.smallpad
          [:button.btn.btn-primary.btn-block
           {:type "button"
            :id "runlots"
            "@click" run-lots}
           "Create 10,000 rows"]]
         [:div.col-sm-6.smallpad
          [:button.btn.btn-primary.btn-block
           {:type "button"
            :id "add"
            "@click" add}
           "Append 1,000 rows"]]
         [:div.col-sm-6.smallpad
          [:button.btn.btn-primary.btn-block
           {:type "button"
            :id "update"
            "@click" update-some}
           "Update every 10th row"]]
         [:div.col-sm-6.smallpad
          [:button.btn.btn-primary.btn-block
           {:type "button"
            :id "clear"
            "@click" clear}
           "Clear"]]
         [:div.col-sm-6.smallpad
          [:button.btn.btn-primary.btn-block
           {:type "button"
            :id "swaprows"
            "@click" swap-rows}
           "Swap rows"]]]]]]
     [:table.table.table-hover.table-striped.test-data
      [:tbody (lit-repeat/repeat @data #(:id %) #(row % select delete))]]
     [:span.preloadicon.glyphicon.glyphicon-remove
      {:aria-hidden "true"}]]))

(def container (js/document.getElementById "main"))

(defn render! []
  (lit/render (app) container))

(def after-reload! render!)

(defn ^:export init! [& _]
  (render!)
  (add-watch data ::data (fn [_ _ _ _] (render!)))
  (add-watch selected ::selected (fn [_ _ _ _] (render!))))
