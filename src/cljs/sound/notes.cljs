(ns sound.notes)


(defn base [n] (partial + n))


(defn lower [scale octaves]
  (base (- (scale)
           (* 12 octaves))))


(def c (base 60))
(def d (base 62))
(def e (base 64))
(def f (base 65))
(def g (base 67))
(def a (base 69))
(def h (base 71))


(defn lookup [note]
  ({:c (lower c 1)
    :d (lower d 1)
    :e (lower e 1)
    :f (lower f 1)
    :g (lower g 1)
    :a (lower a 1)
    :h (lower h 1)} (keyword note)))