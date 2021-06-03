;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at https://mozilla.org/MPL/2.0/.


(ns helins.binf.test.buffer

  {:author "Adam Helinski"}

  (:require [clojure.test.check.clojure-test :as TC.ct]
            [clojure.test.check.generators   :as TC.gen]
            [clojure.test.check.properties   :as TC.prop]
            [helins.binf                     :as binf]
            [helins.binf.buffer              :as binf.buffer]
            [helins.binf.gen                 :as binf.gen]))


;;;;;;;;;;


(TC.ct/defspec copy

  (TC.prop/for-all [[src
                     dest-offset
                     src-offset
                     n-byte]     (TC.gen/let [src         (binf.gen/buffer)
                                              dest-offset (TC.gen/choose 0
                                                                         (binf/limit src))
                                              src-offset  (TC.gen/choose 0
                                                                         (binf/limit src))
                                              n-byte      (TC.gen/choose 0
                                                                         (- (binf/limit src)
                                                                            src-offset))]
                                   [src
                                    dest-offset
                                    src-offset
                                    n-byte])]
    (let [src-limit (binf/limit src)
          sq-offset (repeat dest-offset
                            0)]
      (and (= (seq src)
              (seq (binf.buffer/copy src)))
           (= (seq (concat (seq src)
                           sq-offset))
              (seq (binf.buffer/copy (binf.buffer/alloc (+ src-limit
                                                           dest-offset))
                                     src)))
           (= (seq (concat sq-offset
                           src
                           sq-offset))
              (seq (binf.buffer/copy (binf.buffer/alloc (+ src-limit
                                                           (* 2
                                                              dest-offset)))
                                     dest-offset
                                     src)))
           (= (seq (concat sq-offset
                           (drop src-offset
                                 src)
                           sq-offset))
              (seq (binf.buffer/copy (binf.buffer/alloc (+ (- src-limit
                                                              src-offset)
                                                           (* 2
                                                              dest-offset)))
                                     dest-offset
                                     src
                                     src-offset)))
           (= (seq (concat sq-offset
                           (->> src
                                (drop src-offset)
                                (take n-byte))
                           sq-offset))
              (seq (binf.buffer/copy (binf.buffer/alloc (+ n-byte
                                                           (* 2
                                                              dest-offset)))
                                     dest-offset
                                     src
                                     src-offset
                                     n-byte)))))))
