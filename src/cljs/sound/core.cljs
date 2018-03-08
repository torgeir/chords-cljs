(ns sound.core
  (:require [sound.spec :refer [read-chord]]
            [sound.notes :as notes]
            [cljs.core.async :as async :include-macros true]))


(defn round
  "Rounds n to d decimals."
  [d n]
  (let [mul (Math/pow 10 d)
        div #(/ % mul)]
    (->> n
      (* mul)
      int
      div)))


(defn midi->hz
  "Convert a midi note number to a frequency in hz."
  [note]
  (* 440.0
     (Math/pow 2.0 (/ (- note (notes/a))
                      12.0))))