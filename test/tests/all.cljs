(ns tests.all
  (:require
    [cljs.core.async :refer [<! >! chan alts! timeout]]
    [cljs.test :refer [deftest is testing run-tests async]]
    [district0x.re-frame.window-fx]
    [re-frame.core :refer [reg-event-fx dispatch-sync]])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(reg-event-fx
  ::my-event
  (fn [_ [_ ch]]
    (go (>! ch 1))
    nil))

(reg-event-fx
  ::setup-on-hashchange
  (fn [_ [_ ch]]
    {:window/on-hashchange {:dispatch [::my-event ch]}}))

(deftest tests
  (async done
    (go
      (let [ch (chan)]
        (dispatch-sync [::setup-on-hashchange ch])
        (set! (.-hash js/location) "something")
        (is (= 1 (<! ch)))
        (done)))))