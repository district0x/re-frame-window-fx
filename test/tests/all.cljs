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
    {:window/on-hashchange {:dispatch [::my-event ch]
                            :id ::my-event}}))

(reg-event-fx
  ::stop-on-hashchange
  (fn []
    {:window/stop-on-hashchange {:id ::my-event}}))

(deftest tests
  (async done
    (go
      (let [ch (chan)]
        (testing "Correctly detects hashchange"
          (dispatch-sync [::setup-on-hashchange ch])
          (set! (.-hash js/location) "something")
          (is (= 1 (<! ch))))

        (testing "After stoping listener, shouldn't listen to event anymore"
          (dispatch-sync [::stop-on-hashchange])
          (set! (.-hash js/location) "some-other-thing")
          (is (nil? (first (alts! [ch (timeout 1000)])))))

        (done)))))
