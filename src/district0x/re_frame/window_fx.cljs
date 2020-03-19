(ns district0x.re-frame.window-fx
  (:require
    [cljs.spec.alpha :as s]
    [goog.events :as events]
    [re-frame.core :as re-frame :refer [reg-fx]])
  (:import [goog.events]))


(s/def ::dispatch sequential?)
(s/def ::debounce-ms int?)
(s/def ::id any?)
(s/def ::on-resize-args (s/keys :req-un [::dispatch] :opt-un [::debounce-ms ::id]))

(def *listeners* (atom {}))

(defn- setup-listener! [event-type callback id]
  (let [key (events/listen js/window event-type (if (fn? callback)
                                                  callback
                                                  #(re-frame/dispatch callback)))]
    (when id
      (swap! *listeners* assoc-in [event-type id] key))))


(defn- stop-listener! [event-type id]
  (when-let [key (get-in @*listeners* [event-type id])]
    (events/unlistenByKey key)
    (swap! *listeners* update event-type #(dissoc % id))))


(defn on-resize [{:keys [:dispatch :debounce-ms]} timer]
  (js/clearTimeout @timer)
  (reset! timer (js/setTimeout #(re-frame/dispatch (into [] (concat dispatch [js/window.innerWidth js/window.innerHeight])))
                               (or debounce-ms 166))))


(reg-fx
  :window/on-resize
  (fn [{:keys [:id] :as config}]
    (s/assert ::on-resize-args config)
    (let [timer (atom nil)]
      (setup-listener! events/EventType.RESIZE (partial on-resize config timer) id))))

(reg-fx
  :window/stop-on-resize
  (fn [{:keys [:id]}]
    (stop-listener! events/EventType.RESIZE id)))

(reg-fx
  :window/on-focus
  (fn [{:keys [:dispatch :id]}]
    (setup-listener! events/EventType.FOCUS dispatch id)))

(reg-fx
  :window/stop-on-focus
  (fn [{:keys [:id]}]
    (stop-listener! events/EventType.FOCUS id)))

(reg-fx
  :window/on-blur
  (fn [{:keys [:dispatch :id]}]
    (setup-listener! events/EventType.BLUR dispatch id)))

(reg-fx
  :window/stop-on-blur
  (fn [{:keys [:id]}]
    (stop-listener! events/EventType.BLUR id)))

(reg-fx
  :window/on-hashchange
  (fn [{:keys [:dispatch :id]}]
    (setup-listener! events/EventType.HASHCHANGE dispatch id)))

(reg-fx
  :window/stop-on-hashchange
  (fn [{:keys [:id]}]
    (stop-listener! events/EventType.HASHCHANGE id)))

(reg-fx
  :window/scroll-to
  (fn [[x y]]
    (.scrollTo js/window x y)))

(reg-fx
  :window/set-title
  (fn [title]
    (aset js/document "title" title)))


;; Set the URL hash for the window
;; hash - string representing the hash
(reg-fx
  :window.location/set-hash
  (fn [hash]
    (->> hash str (.encodeURI js/window) (aset js/window "location" "hash"))))


(defn- kw->url
  "URL encode a keyword"
  [kw]
  (->> kw name (.encodeURI js/window)))


;; FX, Set the URL search query for the window
;; m - map of key value pairs to pass
;; Note: keywords/strings are url encoded
(reg-fx
  :window.location/set-search-query
  (fn [m]
    (cond
     (> (count m) 1)
     (->> m
          (mapcat (fn [[k v]] (str (kw->url k) "=" (kw->url v) "&")))
          (aset js/window "location" "search"))
     :else
     (->> seq first (map kw->url) (reduce (fn [[k v]] (str k "=" v)))))))
     
