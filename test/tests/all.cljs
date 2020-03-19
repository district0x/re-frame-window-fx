(ns tests.all
  (:require
   [cuerdas.core :as str]
   [cljs.core.async :refer [<! >! chan alts! timeout]]
   [cljs.test :refer [deftest is testing run-tests async]]
   [day8.re-frame.test :refer [run-test-async wait-for]]
   [district0x.re-frame.window-fx] ;; :window/* & :window.location/*
   [re-frame.core :as re :refer [reg-event-fx dispatch-sync]])
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
          (is (nil? (first (alts! [ch (timeout 1000)]))))
          (set! (.-hash js/location) ""))
        (done)))))


(re/reg-event-fx
  ::set-title
  (fn [_ [_ title]]
    {:window/set-title title}))

(deftest test-window-set-title
  (run-test-async
   (re/dispatch [::set-title "foo"])
   (wait-for
    [::set-title]
    
    (is (= (aget js/document "title") "foo"))
    (re/dispatch [::set-title "bar"])

    (wait-for
     [::set-title]
   
     (is (= (aget js/document "title") "bar"))
     (set! (.-title js/document) "")))))


(re/reg-event-fx
  ::set-hash
  (fn [_ [_ hash]]
    {:window.location/set-hash hash}))

(deftest test-window-location-set-hash
  (run-test-async
   (is (= (aget js/window "location" "hash") ""))
   (re/dispatch [::set-hash "foo"])
   (wait-for
    [::set-hash]
    
    (is (= (aget js/window "location" "hash") "#foo"))
    (re/dispatch [::set-hash "bar"])

    (wait-for
     [::set-hash]
   
     (is (= (aget js/window "location" "hash") "#bar"))
     (set! (.-hash js/location) "")))))
