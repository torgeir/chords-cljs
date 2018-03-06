(ns sound.chord
  (:require [sound.spec :refer [read-chord]]
            [sound.notes :as notes]
            [cljs.core.async :as async :include-macros true]))


(defn round
  "Rounds n to d decimals."
  [d n]
  (let [mul (Math/pow 10 d)
        div #(/ % mul)]
    (->> n (* mul) int div)))


(defn midi->hz
  "Convert a midi note number to a frequency in hz."
  [note]
  (* 440.0
     (Math/pow 2.0 (/ (- note notes/a4)
                      12.0))))


(defn read-chords
  "Read key presses from chan and parse to chords. Buffers key presses for
  creating chords consisting of multiple parts, or creates chord from the last
  key."
  [chan]
  (let [state (atom {})
        buf   (atom [])]
    (async/go-loop []
      (when-let [key (async/<! chan)]
        (let [buffered (conj @buf key)]
          (if-let [chord (read-chord buffered)]
            (do (reset! state chord)
                (reset! buf buffered))
            (if-let [chord (read-chord key)]
              (do (reset! state chord)
                  (reset! buf [key]))
              (swap! buf conj key))))
        (recur)))
    state))


(defn init
  "Called on page load."
  []
  (enable-console-print!)

  (let [key-chan (async/chan)
        state    (read-chords key-chan)
        el       (.querySelector js/document ".chord")]

    (.addEventListener
      js/document
      "keypress"
      #(async/put! key-chan (.fromCharCode js/String (.-keyCode %))))

    (add-watch state :watch #(set! (.-innerHTML el) %4))))