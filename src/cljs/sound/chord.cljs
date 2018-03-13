(ns sound.chord
  (:require [sound.notes :refer [lookup]]))


(def triad
  {:first 1 :third 5 :fifth 8})


(defn- map-values [f map]
  (into {} (for [[k v] map]
             [k (f v)])))


(defn property [p note]
  (-> note p first))


(defn chord [note]
  (let [scale      (-> (:note note) lookup)
        note-type  (property :type note)
        note-pitch (property :pitch note)
        _          (println note-type)
        triad      (cond
                     (= :sharp note-pitch) (map-values inc triad)
                     (= :flat note-pitch)  (map-values dec triad)
                     :else                 triad)]
    (->> (update triad :third (if (= :minor note-type) dec identity))
      (map-values dec)
      (map-values scale)
      ;;((fn [v] (println v) v))
      vals)))


(comment
  (chord {:note "c"})
  (chord {:note "c"})
  (chord {:note "c" :type [:minor "m"]})
  (chord {:note "c" :pitch [:sharp "#"]})
  (chord {:note "c" :pitch [:flat "b"]})
  )