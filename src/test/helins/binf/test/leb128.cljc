;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at https://mozilla.org/MPL/2.0/.


(ns helins.binf.test.leb128

  "Testing LEB128 utilities."

  {:author "Adam Helins"}

  (:require [clojure.test                  :as T]
            [clojure.test.check.properties :as TC.prop]
            [helins.binf                   :as binf]
            [helins.binf.buffer            :as binf.buffer]
            [helins.binf.gen               :as binf.gen]
            [helins.binf.int64             :as binf.int64]
            [helins.binf.leb128            :as binf.leb128]
            [helins.mprop                  :as mprop]))


;;;;;;;;;; int32


(T/deftest u32

  (let [v (binf/view (binf.buffer/alloc 32))]

    (-> v
        (binf/seek 0)
        (binf.leb128/wr-u32 0))

    (T/is (= 1
             (binf/position v)
             (binf.leb128/n-byte-u32 0)))

    (T/is (= 0
             (-> v
                 (binf/seek 0)
                 (binf.leb128/rr-u32))))


    (-> v
        (binf/seek 0)
        (binf.leb128/wr-u32 4294967295))

    (T/is (= (binf.leb128/n-byte-max 32)
             (binf/position v)
             (binf.leb128/n-byte-u32 4294967295)))

    (T/is (= 4294967295
             (-> v
                 (binf/seek 0)
                 (binf.leb128/rr-u32))))))



(T/deftest i32

  (let [v (binf/view (binf.buffer/alloc 32))]

    (-> v
        (binf/seek 0)
        (binf.leb128/wr-i32 0))

    (T/is (= 1
             (binf/position v)
             (binf.leb128/n-byte-i32 0)))

    (T/is (= 0
             (-> v
                 (binf/seek 0)
                 (binf.leb128/rr-i32))))

    (-> v
        (binf/seek 0)
        (binf.leb128/wr-i32 2147483647))

    (T/is (= (binf.leb128/n-byte-max 32)
             (binf/position v)
             (binf.leb128/n-byte-i32 2147483647)))

    (T/is (= 2147483647
             (-> v
                 (binf/seek 0)
                 (binf.leb128/rr-i32))))

    (-> v
        (binf/seek 0)
        (binf.leb128/wr-i32 -2147483648))

    (T/is (= (binf.leb128/n-byte-max 32)
             (binf/position v)
             (binf.leb128/n-byte-i32 -2147483648)))

    (T/is (= -2147483648
             (-> v
                 (binf/seek 0)
                 (binf.leb128/rr-i32))))

    (-> v
        (binf/seek 0)
        (binf.leb128/wr-i32 -42))

    (T/is (= 1
             (binf/position v)
             (binf.leb128/n-byte-i32 0)))

    (T/is (= -42
             (-> v
                 (binf/seek 0)
                 (binf.leb128/rr-i32))))

    (-> v
        (binf/seek 0)
        (binf/wr-b8 0x7F))

    (T/is (= 1
             (binf/position v)
             (binf.leb128/n-byte-i32 0)))

    (T/is (= -1
             (-> v
                 (binf/seek 0)
                 (binf.leb128/rr-i32))))))


;;;;;;;;;; int64


(T/deftest u64

  (let [v (binf/view (binf.buffer/alloc 32))]

    (-> v
        (binf/seek 0)
        (binf.leb128/wr-u64 (binf.int64/u* 0)))

    (T/is (= 1
             (binf/position v)
             (binf.leb128/n-byte-u64 (binf.int64/u* 0))))

    (T/is (= (binf.int64/u* 0)
             (-> v
                 (binf/seek 0)
                 (binf.leb128/rr-u64))))

    (-> v
        (binf/seek 0)
        (binf.leb128/wr-u64 (binf.int64/u* 18446744073709551615)))

    (T/is (= (binf.leb128/n-byte-max 64)
             (binf/position v)
             (binf.leb128/n-byte-u64 (binf.int64/u* 18446744073709551615))))

    (T/is (= (binf.int64/u* 18446744073709551615)
             (-> v
                 (binf/seek 0)
                 (binf.leb128/rr-u64))))))



(T/deftest i64

  (let [v (binf/view (binf.buffer/alloc 32))]

    (-> v
        (binf/seek 0)
        (binf.leb128/wr-i64 (binf.int64/i* 0)))

    (T/is (= 1
             (binf/position v)
             (binf.leb128/n-byte-i64 (binf.int64/i* 0))))

    (T/is (= (binf.int64/i* 0)
             (-> v
                 (binf/seek 0)
                 (binf.leb128/rr-i64))))

    (-> v
        (binf/seek 0)
        (binf.leb128/wr-i64 (binf.int64/i* 9223372036854775807)))

    (T/is (= (binf.leb128/n-byte-max 64)
             (binf/position v)
             (binf.leb128/n-byte-i64 (binf.int64/i* 9223372036854775807))))

    (T/is (= (binf.int64/i* 9223372036854775807)
             (-> v
                 (binf/seek 0)
                 (binf.leb128/rr-i64))))

    (-> v
        (binf/seek 0)
        (binf.leb128/wr-i64 (binf.int64/i* -9223372036854775808)))

    (T/is (= (binf.leb128/n-byte-max 64)
             (binf/position v)
             (binf.leb128/n-byte-i64 (binf.int64/i* -9223372036854775808))))

    (T/is (= (binf.int64/i* -9223372036854775808)
             (-> v
                 (binf/seek 0)
                 (binf.leb128/rr-i64))))

    (-> v
        (binf/seek 0)
        (binf.leb128/wr-i64 (binf.int64/i* -42)))

    (T/is (= 1
             (binf/position v)
             (binf.leb128/n-byte-i64 (binf.int64/i* -42))))

    (T/is (= (binf.int64/i* -42)
             (-> v
                 (binf/seek 0)
                 (binf.leb128/rr-i64))))

    (-> v
        (binf/seek 0)
        (binf/wr-b8 0x7F))

    (T/is (= 1
             (binf/position v)
             (binf.leb128/n-byte-i64 (binf.int64/i* -1))))

    (T/is (= (binf.int64/i* -1)
             (-> v
                 (binf/seek 0)
                 (binf.leb128/rr-i64))))))


;;;;;;;;;; Generative testing


(def view-gen
     (-> (binf.leb128/n-byte-max 64)
         binf.buffer/alloc
         binf/view))


(mprop/deftest gen-i32

  (TC.prop/for-all [i32 binf.gen/i32]
    (= i32
       (-> view-gen
           (binf/seek 0)
           (binf.leb128/wr-i32 i32)
           (binf/seek 0)
           binf.leb128/rr-i32))))



(mprop/deftest gen-u32

  (TC.prop/for-all [u32 binf.gen/u32]
    (= u32
       (-> view-gen
           (binf/seek 0)
           (binf.leb128/wr-u32 u32)
           (binf/seek 0)
           binf.leb128/rr-u32))))



(mprop/deftest gen-i64

  (TC.prop/for-all [i64 binf.gen/i64]
    (= i64
       (-> view-gen
           (binf/seek 0)
           (binf.leb128/wr-i64 i64)
           (binf/seek 0)
           binf.leb128/rr-i64))))



(mprop/deftest gen-u64

  (TC.prop/for-all [u64 binf.gen/u64]
    (= u64
       (-> view-gen
           (binf/seek 0)
           (binf.leb128/wr-u64 u64)
           (binf/seek 0)
           binf.leb128/rr-u64))))
