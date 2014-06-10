(ns minesweeper.ui
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [cljs.core.async :refer [put! chan <! >! close! timeout]]
            [minesweeper.game :refer [create-game reveal-tile]]))

(def app-data (atom (create-game {:cols 16 :rows 16 :mines 48})))
(def app-history (atom [@app-data]))

(add-watch app-data :history
           (fn [key ref old new]
             (when-not (= (last @app-history) new)
               (swap! app-history conj new))))

(defn undo []
  (swap! app-history pop)
  (reset! app-data (last @app-history)))

(defn tile-view [tile owner opts]
  (reify
    om/IRender
    (render [_]
      (if (:revealed? tile)
        (dom/div #js {:className (str "tile" (when (:mine? tile) " mine"))}
                 (when (< 0 (:threat-count tile)) (:threat-count tile)))
        (dom/div #js {:className "tile"
                      :onClick (fn [e]
                                 (put! (:moves opts) (:id @tile)))
                      :onContextMenu (fn [e]
                                       (om/transact! tile #(assoc % :maybe? true))
                                       (.preventDefault e))}
                 (dom/div #js {:className "lid"}
                          (when (:maybe? tile) "?")))))))

(defn line-view [tiles owner opts]
  (reify
    om/IRender
    (render [_]
      (apply dom/div #js {:className "row"}
             (map #(om/build tile-view % {:opts opts}) tiles)))))

(defn board-view [game owner opts]
  (reify
    om/IWillMount
    (will-mount [_]
      (go-loop []
               (let [id (<! (:moves opts))]
                 (om/transact! game #(reveal-tile % id)))
               (recur)))
    om/IRender
    (render [_]
      (apply dom/div #js {:className "board"}
             (map #(om/build line-view % {:opts opts})
                  (partition (:cols game) (:tiles game)))))))

(om/root
 board-view
 app-data
 {:target (.getElementById js/document "board")
  :opts {:moves (chan)}})
