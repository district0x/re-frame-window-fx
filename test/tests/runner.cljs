(ns tests.runner
  (:require
    [doo.runner :refer-macros [doo-tests]]
    [tests.all]))

(enable-console-print!)

(doo-tests 'tests.all)

