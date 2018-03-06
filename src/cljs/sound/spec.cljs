(ns sound.spec
  (:require [clojure.spec.alpha :as s]
            [clojure.test.check.generators :as gen]))

(defn number
  "Parse str to js/Number, or nil."
  [v]
  (if-let [num (js/Number v)]
    (when-not (js/isNaN num)
      num)))


(s/def ::note (set "cdefgahc"))


(s/def ::type (s/alt
                :dim (set "D")
                :maj (set "M")
                :minor (set "m")))


(s/def ::pitch (s/alt
                 :sharp (set "#")
                 :flat  (set "b")))


(s/def ::factor (s/+ (s/conformer (comp #(or % :cljs.spec.alpha/invalid)
                                        #{1 3 6 7 9 11}
                                        number))))


(s/def ::sus (s/cat
               :marker (set "s")
               :suspension (s/conformer number)))


(s/def ::bass (s/cat
                :marker (set "/")
                :note ::note
                :pitch (s/? ::pitch)))


(s/def ::chord (s/cat
                 :note ::note
                 :pitch  (s/? ::pitch)
                 :type   (s/? ::type)
                 :factor (s/? ::factor)
                 :sus    (s/? ::sus)
                 :bass   (s/? ::bass)))


(defn read-chord
  "Read string of chars to a chord."
  [chars]
  (let [chord (s/conform ::chord (vec chars))]
    (when-not (= chord :cljs.spec.alpha/invalid)
      chord)))
