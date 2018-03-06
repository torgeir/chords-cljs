(ns sound.spec-test
  (:require [sound.spec :refer [read-chord]]
            [cljs.test :refer [deftest is] :include-macros true]))


(deftest reads-chords-from-string
  (is (= (read-chord "c")        {:note "c"}))
  (is (= (read-chord "c7")       {:note "c" :factor [7]}))
  (is (= (read-chord "cD")       {:note "c" :type [:dim "D"]}))
  (is (= (read-chord "cM")       {:note "c" :type [:maj "M"]}))
  (is (= (read-chord "cm")       {:note "c" :type [:minor "m"]}))
  (is (= (read-chord "cb")       {:note "c" :pitch [:flat "b"]}))
  (is (= (read-chord "c/d")      {:note "c" :bass {:marker "/" :note "d"}}))
  (is (= (read-chord "cs2")      {:note "c" :sus {:marker "s" :suspension 2}}))
  (is (= (read-chord "g#D")      {:note "g" :pitch [:sharp "#"] :type [:dim "D"]}))
  (is (= (read-chord "c#ms2/d")  {:note "c" :pitch [:sharp "#"] :type [:minor "m"] :sus {:marker "s" :suspension 2} :bass {:marker "/" :note "d"}}))
  (is (= (read-chord "c#ms2/d#") {:note "c" :pitch [:sharp "#"] :type [:minor "m"] :sus {:marker "s" :suspension 2}
                                  :bass {:marker "/" :note "d" :pitch [:sharp "#"]}})))