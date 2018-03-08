(ns sound.chord-test
  (:require [sound.chord :refer [chord]]
            [cljs.test :refer [deftest is] :include-macros true]))


(deftest resolves-c-major-triad
  (is (= (chord {:note "c"})
         [60 64 67])))