(ns minesweeper.game)

(defn create-game [{:keys [rows cols mines]}]
  {:cols cols
   :rows rows
   :playing-time 0
   :tiles (->> (concat (repeat mines {:mine? true :revealed? false})
                       (repeat (- (* rows cols) mines) {:revealed? false}))
               shuffle
               (map-indexed #(assoc %2 :id %1))
               (mapv identity))})

(defn- on-w-edge? [game tile]
  (= 0 (mod tile (:cols game))))

(defn- on-e-edge? [game tile]
  (= (dec (:cols game)) (mod tile (:cols game))))

(defn- idx [game tile]
  (if (not (contains? (:tiles game) tile))
    nil
    tile))

(defn nw [game tile]
  (if (on-w-edge? game tile)
    nil
    (idx game (- tile (:cols game) 1))))

(defn n [game tile]
  (idx game (- tile (:cols game))))

(defn ne [game tile]
  (if (on-e-edge? game tile)
    nil
    (idx game (inc (- tile (:cols game))))))

(defn e [game tile]
  (if (on-e-edge? game tile)
    nil
    (idx game (inc tile))))

(defn se [game tile]
  (if (on-e-edge? game tile)
    nil
    (idx game (+ tile (:cols game) 1))))

(defn s [game tile]
  (idx game (+ tile (:cols game))))

(defn sw [game tile]
  (if (on-w-edge? game tile)
    nil
    (idx game (dec (+ tile (:cols game))))))

(defn w [game tile]
  (if (on-w-edge? game tile)
    nil
    (idx game (dec tile))))

(def directions [nw n ne e se s sw w])

(defn neighbours [game tile]
  (->> directions
       (keep #(get-in game [:tiles (% game tile)]))))

(defn get-mine-count [game tile]
  (->> (neighbours game tile)
       (filter :mine?)
       count))

(defn mine? [game tile]
  (:mine? (get-in game [:tiles tile])))

(defn safe? [game]
  (let [tiles (:tiles game)
        mines (filter :mine? tiles)]
    (and (= 0 (count (filter :revealed? mines)))
         (= (- (count tiles) (count mines))
            (count (filter :revealed? tiles))))))

(defn game-over? [game]
  (or (:safe? game) (:dead? game)))

(defn add-threat-count [game tile]
  (assoc-in game [:tiles tile :threat-count] (get-mine-count game tile)))

(defn reveal-adjacent-safe-tiles [game tile]
  (if (mine? game tile)
    game
    (let [game (assoc-in (add-threat-count game tile) [:tiles tile :revealed?] true)]
      (-> (if (= 0 (get-in game [:tiles tile :threat-count]))
            (reduce (fn [game pos]
                      (let [adjacent (get-in game [:tiles pos])]
                        (if (and (not (nil? adjacent))
                                 (not (:revealed? adjacent)))
                          (reveal-adjacent-safe-tiles game pos)
                          game))) game (keep #(% game tile) directions))
            game)))))

(defn attempt-winning [game]
  (if (safe? game)
    (assoc game :safe? true)
    game))

(defn reveal-mine [tile]
  (if (:mine? tile)
    (assoc tile :revealed? true)
    tile))

(defn reveal-mines [game]
  (update-in game [:tiles] #(map reveal-mine %)))

(defn reveal-tile [game tile]
  (let [updated (if (nil? (get-in game [:tiles tile]))
                  game
                  (assoc-in game [:tiles tile :revealed?] true))]
    (if (mine? updated tile)
      (-> updated
          (assoc :dead? true)
          reveal-mines)
      (-> updated
          (add-threat-count tile)
          (reveal-adjacent-safe-tiles tile)
          attempt-winning))))
