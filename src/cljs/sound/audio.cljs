(ns sound.audio
  (:require [sound.chord :refer [chord]]
            [sound.core :refer [midi->hz]]))


(defn audio-context []
  (if js/window.AudioContext
    (new js/window.AudioContext)
    (new js/window.webkitAudioContext)))


(defn graph
  ([in out] {:in in :out out})
  ([singleton] (graph singleton singleton)))


(defn source [node] (graph nil node))


(defn sink [node] (graph node nil))


(defn run-synth [synth ctx at duration]
  (synth ctx at duration))


(defn gain
  "Create a gain node from a context with the value set at time. Takes time plus
  ramp to reach value."
  [value]
  (fn [ctx at duration]
    (graph
      (doto (.createGain ctx)
        (-> .-gain (.setValueAtTime value at))))))


(defn osc
  "Creates an oscillating synth."
  [type value]
  (fn [ctx at duration]
    (source
      (doto (.createOscillator ctx)
        (-> .-frequency .-value (set! 0))
        (-> .-frequency (.setValueAtTime value at))
        (-> .-type (set! type))
        (.start at)
        (.stop (+ at duration 1))))))


(def sine (partial osc "sine"))


(def sawtooth (partial osc "sawtooth"))


(def square (partial osc "square"))


(def triangle (partial osc "triangle"))


(defn current-time
  "The current time of the context."
  [ctx]
  (.-currentTime ctx))


(defn destination
  "The context's destination as a synth."
  [ctx at duration]
  (sink (.-destination ctx)))


(defn connect-nodes
  "Connect two nodes nodes."
  [source dest]
  (.connect (:out source) (:in dest))
  (graph (:in source) (:out dest)))


(defn interpose-parallel
  "Connect graphs in parallel between source and dest."
  [source dest & graphs]
  (doseq [g graphs]
    (when (:in g)
      (.connect (:out source) (:in g)))
    (.connect (:out g) (:in dest)))
  (graph (:in source) (:out dest)))


(defn apply-graph
  [f & synths]
  (fn [ctx at duration]
    (->> synths
      (map #(run-synth % ctx at duration))
      (apply f))))


(defn series
  [& nodes]
  (reduce #(apply-graph connect-nodes %1 %2) nodes))


(defn parallel
  [& graphs]
  (apply apply-graph interpose-parallel (gain 1) (gain 1) graphs))


(defn env
  [& edges]
  (fn [ctx at duration]
    (let [node (.createGain ctx)]
      (-> node .-gain (.setValueAtTime 0 at))
      (loop [at at, edges edges]
        (when-let [[[dx value] & rest] edges]
          (-> node .-gain (.linearRampToValueAtTime value (+ at dx)))
          (recur (+ at dx) rest)))
      (graph node))))


(defonce context (audio-context))


(defn play
  "Play a frequency through an audio context."
  [synth]
  (-> synth
    (series destination)
    (run-synth context (current-time context) 1)))


(defn piano
  [hz]
  (letfn [(harmony [n proportion]
            (series (sine (* n hz))
                    (gain 0.05)
                    (env [0.03 1] [(* n proportion) 0.0])))]
    (parallel (harmony 1 1.0)
              (harmony 2 0.6)
              (harmony 3 0.4)
              (harmony 4 0.3)
              (harmony 5 0.2)
              (harmony 6 0.1))))

(comment (->> (chord {:note "c"})
           (map midi->hz)
           (map piano)
           (map #(play %))
           doall))