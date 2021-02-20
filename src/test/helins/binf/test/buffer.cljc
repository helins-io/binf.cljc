(ns helins.binf.test.buffer

  ""

  {:author "Adam Helinski"}

  (:require [clojure.test       :as t]
            [helins.binf        :as binf]
            [helins.binf.buffer :as binf.buffer]))


;;;;;;;;;;


(defn make-view

  ([]

   (make-view binf.buffer/alloc))


  ([make-buffer]

   (let [view (binf/view (make-buffer 5))]
     (dotimes [_ 5]
       (binf/wr-b8 view
                   1))
     view)))



(def copy-target
     (concat (repeat 5
                     0)
             (repeat 2
                     1)
             (repeat 3
                     0)))



(t/deftest copy-buffer

  (t/is (= (seq (binf/to-buffer (make-view)))
           (seq (binf.buffer/copy (binf/to-buffer (make-view))))
           #?(:cljs (seq (binf/to-buffer (make-view binf.buffer/alloc-shared))))
           #?(:cljs (seq (binf.buffer/copy (binf/to-buffer (make-view binf.buffer/alloc-shared))))))
        "Cloning")

  (t/is (= (concat (repeat 5
                           0)
                   (repeat 5
                           1))
           (seq (binf.buffer/copy (binf.buffer/alloc 10)
                                  5
                                  (binf/to-buffer (make-view))))
           #?(:cljs (seq (binf.buffer/copy (binf.buffer/alloc-shared 10)
                                           5
                                           (binf/to-buffer (make-view binf.buffer/alloc-shared))))))
        "Without offset nor length")

  (t/is (= (concat (repeat 5
                           0)
                   (repeat 3
                           1)
                   (repeat 2
                           0))
           (seq (binf.buffer/copy (binf.buffer/alloc 10)
                                  5
                                  (binf/to-buffer (make-view))
                                  2))
           #?(:cljs (seq (binf.buffer/copy (binf.buffer/alloc-shared 10)
                                           5
                                           (binf/to-buffer (make-view binf.buffer/alloc-shared))
                                           2))))
        "With offset")


  (t/is (= copy-target
           (seq (binf.buffer/copy (binf.buffer/alloc 10)
                                  5
                                  (binf/to-buffer (make-view))
                                  2
                                  2))
           #?(:cljs (seq (binf.buffer/copy (binf.buffer/alloc-shared 10)
                                           5
                                           (binf/to-buffer (make-view binf.buffer/alloc-shared))
                                           2
                                           2))))
        "With offset and length"))


