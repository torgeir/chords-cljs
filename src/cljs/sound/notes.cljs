(ns sound.notes)


(defn base [n] (partial + n))


(def c (base 60))
(def d (base 62))
(def e (base 64))
(def f (base 65))
(def g (base 67))
(def a (base 69))
(def h (base 71))


(defn lookup [note]
  ({:c c :d d :e e :f f :g g :a a :h h} (keyword note)))