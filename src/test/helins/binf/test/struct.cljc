;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at https://mozilla.org/MPL/2.0/.


(ns helins.binf.test.struct

  {:author "Adam Helins"}

  (:require [clojure.test       :as t]
            [helins.binf.struct :as binf.struct]))


;;;;;;;;;;


(t/deftest unnested

  (t/is (= {:binf.struct/align   8
            :binf.struct/layout  [:a
                                  :b
                                  :c]
            :binf.struct/offset+ [0
                                  2
                                  8]
            :binf.struct/n-byte  16}
           (binf.struct/c 8
                          [(binf.struct/u8  :a)
                           (binf.struct/i16 :b)
                           (binf.struct/f64 :c)])))

  (t/is (= {:binf.struct/align   4
            :binf.struct/layout  [:a
                                  :b
                                  :c
                                  :d]
            :binf.struct/offset+ [0
                                  2
                                  4
                                  12]
            :binf.struct/n-byte  16}
           (binf.struct/c 4
                          [(binf.struct/i8  :a)
                           (binf.struct/u16 :b)
                           (binf.struct/i64 :c)
                           (binf.struct/u8  :d)])))
  )
