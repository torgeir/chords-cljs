(ns sound.audio
  (:require ;;[cljs-bach.synthesis :as bach]
   ))


(defn gain
  "Create a gain node from a context with the value set at time. Takes time plus
  ramp to reach value. Return gain node and function to stop it."
  [ctx value time ramp]
  (let [gain (.createGain ctx)]
    (doto (.-gain gain)
      (.setValueAtTime 0 time)
      (.linearRampToValueAtTime value (+ time ramp)))
    [gain #(.linearRampToValueAtTime (.-gain gain) 0 %)]))


(defn sine
  "Create sine wave at the frequency of value set at time. Return sine
  oscillator node an function to stop it."
  [ctx value time]
  (let [sine (.createOscillator ctx)]
    (doto (.-frequency sine)
      (.setValueAtTime value time))
    [sine #(.stop sine %)]))


(defn connect
  "Connect successive nodes and an audio context destination."
  [ctx & nodes]
  (doseq [[source dest] (->> (.-destination ctx)
                          (conj (vec nodes))
                          (partition 2 1))]
    (.connect source dest)))


(defn play
  "Play a frequency through an audio context. Return function to stop it."
  [ctx hz]
  (let [time             (.-currentTime ctx)
        [sine stop-sine] (sine ctx hz time)
        [gain stop-gain] (gain ctx 0.25 time 0.01)]
    (connect ctx sine gain)
    (.start sine)
    #(let [end (.-currentTime ctx)]
       (stop-gain (+ end 0.01))
       (stop-sine (+ end 0.02)))))
