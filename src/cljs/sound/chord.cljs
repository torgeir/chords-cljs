(ns sound.chord
  (:require [sound.notes :refer [lookup]]))


(def triad
  {:first 1 :third 5 :fifth 8})


(defn- map-values [f map]
  (into {} (for [[k v] map]
             [k (f v)])))


(defn chord [note]
  (let [scale     (-> (:note note) lookup)
        type-map  (into {} [(:type note)])
        pitch-map (into {} [(:pitch note)])
        is-minor  (:minor type-map)
        is-sharp  (:sharp pitch-map)
        is-flat   (:flat pitch-map)
        triad     (cond
                    is-sharp (map-values inc triad)
                    is-flat  (map-values dec triad)
                    :else    triad)]
    (->> (update triad :third (if is-minor dec identity))
      (map-values dec)
      (map-values scale)
      vals)))

(comment
  (chord {:note "c"})
  (chord {:note "c" :type [:minor "m"]})
  (chord {:note "c" :pitch [:sharp "#"]})
  (chord {:note "c" :pitch [:flat "b"]})
  )