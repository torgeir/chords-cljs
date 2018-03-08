(ns sound.core-test
  (:require [sound.notes :as notes]
            [sound.core :refer [midi->hz round]]
            [sound.ui :refer [read-chords]]
            [cljs.test :refer [deftest testing is async] :include-macros true]
            [cljs.core.async :as a :include-macros true]
            [cljs.core.async :as async]))


(defn test-async
  "Asynchronous test awaiting ch to produce a value or close."
  [ch]
  (async done
         (a/take! ch (fn [_] (done)))))


(defn is-chord [input expected]
  (let [s (-> input seq a/to-chan read-chords)]
    (test-async
      (a/go
        (is (= expected @s))))))


(deftest midi-c3-is-130_81
  (is (= (round 2 (midi->hz (notes/c)))
         261.62)))


(deftest midi-a4-is-440-hz
  (is (= (midi->hz (notes/a))
         440)))


(deftest reads-single-chord
  (is-chord "c" {:note "c"}))


(deftest read-single-sharp-chord
  (is-chord "c#" {:note "c" :pitch [:sharp "#"]}))


(deftest reads-one-chord-then-another
  (is-chord "cd" {:note "d"}))


(deftest reads-one-chord-with-sharp-then-another-with-flat
  (is-chord "c#db" {:note "d" :pitch [:flat "b"]}))


(deftest reads-single-minor-chord
  (is-chord "em" {:note "e" :type [:minor "m"]}))


(deftest reads-chords-with-bass-note
  (is-chord "c/a" {:note "c" :bass {:marker "/" :note "a"}}))