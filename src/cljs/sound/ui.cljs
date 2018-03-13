(ns sound.ui
  (:require [sound.chord :refer [chord]]
            [sound.audio :refer [series sine gain env piano play]]
            [sound.core :refer [midi->hz round]]
            [sound.spec :refer [read-chord]]
            [cljs.core.async :as async :include-macros true]))


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

    (add-watch state :watch (fn [_ _ _ note]
                              (set! (.-innerHTML el) note)
                              (->> note
                                (chord)
                                (map midi->hz)
                                (map piano)
                                (map play)
                                doall)))))
