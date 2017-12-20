(ns district0x.re-frame.window-fx
  (:require
    [cljs.spec.alpha :as s]
    [goog.events :as events]
    [re-frame.core :as re-frame :refer [reg-fx]]))

(s/def ::dispatch sequential?)
(s/def ::debounce-ms int?)
(s/def ::on-resize-args (s/keys :req-un [::dispatch] :opt-un [::debounce-ms]))

(defn on-resize [{:keys [:dispatch :debounce-ms]} timer]
  (js/clearTimeout @timer)
  (reset! timer (js/setTimeout #(re-frame/dispatch (into [] (concat dispatch [js/window.innerWidth js/window.innerHeight])))
                               (or debounce-ms 166))))

(reg-fx
  :window/on-resize
  (fn [config]
    (s/assert ::on-resize-args config)
    (let [timer (atom nil)]
      (events/listen js/window events/EventType.RESIZE (partial on-resize config timer)))))

(reg-fx
  :window/on-focus
  (fn [{:keys [:dispatch]}]
    (events/listen js/window events/EventType.FOCUS #(re-frame/dispatch dispatch))))

(reg-fx
  :window/on-blur
  (fn [{:keys [:dispatch]}]
    (events/listen js/window events/EventType.BLUR #(re-frame/dispatch dispatch))))

(reg-fx
  :window/on-hashchange
  (fn [{:keys [:dispatch]}]
    (events/listen js/window events/EventType.HASHCHANGE #(re-frame/dispatch dispatch))))

(reg-fx
  :window/scroll-to
  (fn [[x y]]
    (.scrollTo js/window x y)))

