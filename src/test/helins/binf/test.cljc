;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at https://mozilla.org/MPL/2.0/.


(ns helins.binf.test

  "Testing core view utilities."

  {:author "Adam Helins"}

  #?(:clj (:import java.nio.CharBuffer))
  (:require [clojure.string]
            [clojure.test                    :as t]
            [clojure.test.check.clojure-test :as TC.ct]
            [clojure.test.check.generators   :as TC.gen]
            [clojure.test.check.properties   :as TC.prop]
            [helins.binf                     :as binf]
            [helins.binf.buffer              :as binf.buffer]
            [helins.binf.gen                 :as binf.gen]
            [helins.binf.int                 :as binf.int]
            [helins.binf.int64               :as binf.int64]
            #?(:clj [helins.binf.native      :as binf.native])
            [helins.binf.test.buffer         :as binf.test.buffer]
            [helins.binf.test.string         :as binf.test.string]))


#?(:clj (set! *warn-on-reflection*
              true))


;;;;;;;;;; Miscellaneous


(def gen-endianess

  ""

  (TC.gen/elements [:big-endian
                    :little-endian]))



(defn eq-float

  "Computes equality for floats where NaN is equal to itself."

  [x y]

  (if (Double/isNaN x)
    (Double/isNaN y)
    (= x
       y)))


;;;;;;;;;; Creating views


(def offset
     4)


(def size
     16)


(def size-2
     4)


(def view
     (-> (binf.buffer/alloc size)
         binf/view
         (binf/endian-set :little-endian)))


#?(:clj (def view-native
             (binf/endian-set (binf.native/view size)
                              :little-endian)))


#?(:cljs (def view-shared
              (-> (binf.buffer/alloc-shared size)
                  binf/view
                  (binf/endian-set :little-endian))))


(t/deftest offset-view
  (let [view (-> (binf.buffer/alloc 64)
                 (binf/view 32
                            16)
                 (binf/endian-set :little-endian))]
    (binf/wa-b8 view 0
                42)
    (t/is (= 42
             (binf/ra-u8 view 0)))))


(t/deftest buffer->view

  ;; Without offset nor size
  
  (t/is (= 0
           (binf/buffer-offset view)
           #?(:cljs (binf/buffer-offset view-shared))))
  (t/is (= 0
           (binf/position view)
           #?(:clj (binf/position view-native))
           #?(:cljs (binf/position view-shared))))
  (t/is (= size
           (binf/limit view)
           #?(:clj (binf/limit view-native))
           #?(:cljs (binf/limit view-shared))))
  (t/is (= size
           (binf/remaining view)
           #?(:clj (binf/remaining view-native))
           #?(:cljs (binf/remaining view-shared))))

  ;; With offset

  (let [v (binf/view (binf.buffer/alloc size)
                     offset)
        #?@(:cljs [v-shared (binf/view (binf.buffer/alloc-shared size)
                                       offset)])]
    (t/is (= offset
             (binf/buffer-offset v)
             #?(:cljs (binf/buffer-offset v-shared))))
    (t/is (= 0
             (binf/position v)
             #?(:cljs (binf/position v-shared))))
    (t/is (= (- size
                offset)
             (binf/limit v)
             #?(:cljs (binf/limit v-shared))))
    (t/is (= (- size
                offset)
             (binf/remaining v)
             #?(:cljs (binf/remaining v-shared)))))

  ;; With offset and size

  (let [v (binf/view (binf.buffer/alloc size)
                     offset
                     size-2)
        #?@(:cljs [v-shared (binf/view (binf.buffer/alloc-shared size)
                                       offset
                                       size-2)])]
    (t/is (= offset
             (binf/buffer-offset v)
             #?(:cljs (binf/buffer-offset v-shared))))
    (t/is (= 0
             (binf/position v)
             #?(:cljs (binf/position v-shared))))
    (t/is (= size-2
             (binf/limit v)
             #?(:cljs (binf/limit v-shared))))
    (t/is (= size-2
             (binf/remaining v)
             #?(:cljs (binf/remaining v-shared))))))



(t/deftest view->view

  ;; Without offset nor size
  
  (let [v (binf/view view)]
    (t/is (= :little-endian
             (binf/endian-get v))
          "Endianess is duplicated")
    (t/is (= 0
             (binf/buffer-offset v)))
    (t/is (= 0
             (binf/position v)))
    (t/is (= size
             (binf/limit v)))
    (t/is (= size
             (binf/remaining v))))

  ;; With offset

  (let [v (binf/view view
                     offset)
        #?@(:clj [v-native (binf/view view-native
                                      offset)])]
    (t/is (= :little-endian
             (binf/endian-get v))
          "Endianess is duplicated")
    #?(:clj (t/is (= :little-endian
                     (binf/endian-get v-native))
                  "Endianess is duplicated in native view"))
    (t/is (= offset
             (binf/buffer-offset v)))
    (t/is (= 0
             (binf/position v)
             #?(:clj (binf/position v-native))))
    (t/is (= (- size
                offset)
             (binf/limit v)
             #?(:clj (binf/limit v-native))))
    (t/is (= (- size
                offset)
             (binf/remaining v)
             #?(:clj (binf/remaining v-native)))))

  ;; With offset and size

  (let [v (binf/view view
                     offset
                     size-2)
        #?@(:clj [v-native (binf/view view-native
                                      offset
                                      size-2)])]
    (t/is (= :little-endian
             (binf/endian-get v))
          "Endianess is duplicated")
    #?(:clj (t/is (= :little-endian
                     (binf/endian-get v-native))
                  "Endianess is duplicated in native view"))
    (t/is (= offset
             (binf/buffer-offset v)))
    (t/is (= 0
             (binf/position v)
             #?(:clj (binf/position v-native))))
    (t/is (= size-2
             (binf/limit v)
             #?(:clj (binf/limit v-native))))
    (t/is (= size-2
             (binf/remaining v)
             #?(:clj (binf/remaining v-native))))))


;;;;;;;;; Numerical R/W


;; TODO. Ensure position has been moved correctly in relative R/W + not at all in absolute R/W.
;; TODO. Randomize endianess.


(def view-size
     1024)



(defn gen-write

  ""

  [src n-byte]

  (TC.gen/let [start (TC.gen/choose 0
                                    (- view-size
                                       n-byte))
               size  (TC.gen/choose n-byte
                                    (- view-size
                                       start))
               pos   (TC.gen/choose 0
                                    (- size
                                       n-byte))]
    [pos
     (binf/view src
                start
                size)]))



(defn prop-absolute

  ""


  ([src gen n-byte ra wa]

   (prop-absolute src
                  gen
                  n-byte
                  ra
                  wa
                  =))


  ([src gen n-byte ra wa eq]

   (TC.prop/for-all [x      gen
                     [pos
                      view] (gen-write src
                                       n-byte)]
     (eq x
         (-> view
             (wa pos
                 x)
             (ra pos))))))



(defn prop-relative

  ""


  ([src gen n-byte ra wa]

   (prop-relative src
                  gen
                  n-byte
                  ra
                  wa
                  =))


  ([src gen n-byte rr wr eq]

   (TC.prop/for-all [x      gen
                     [pos
                      view] (gen-write src
                                       n-byte)]
     (eq x
         (-> view
             (binf/seek pos)
             (wr x)
             (binf/seek (- (binf/position view)
                          n-byte))
             rr)))))


(def src
     (binf.buffer/alloc view-size))



(TC.ct/defspec rwa-i8

  (prop-absolute src
                 binf.gen/i8
                 1
                 binf/ra-i8
                 binf/wa-b8))


(TC.ct/defspec rwa-i16

  (prop-absolute src
                 binf.gen/i16
                 2
                 binf/ra-i16
                 binf/wa-b16))


(TC.ct/defspec rwa-i32

  (prop-absolute src
                 binf.gen/i32
                 4
                 binf/ra-i32
                 binf/wa-b32))


(TC.ct/defspec rwa-i64

  (prop-absolute src
                 binf.gen/i64
                 8
                 binf/ra-i64
                 binf/wa-b64))


(TC.ct/defspec rwa-u8

  (prop-absolute src
                 binf.gen/u8
                 1
                 binf/ra-u8
                 binf/wa-b8))


(TC.ct/defspec rwa-u16

  (prop-absolute src
                 binf.gen/u16
                 2
                 binf/ra-u16
                 binf/wa-b16))


(TC.ct/defspec rwa-u32

  (prop-absolute src
                 binf.gen/u32
                 4
                 binf/ra-u32
                 binf/wa-b32))


(TC.ct/defspec rwa-u64

  (prop-absolute src
                 binf.gen/u64
                 8
                 binf/ra-u64
                 binf/wa-b64))


(TC.ct/defspec rwr-i8

  (prop-relative src
                 binf.gen/i8
                 1
                 binf/rr-i8
                 binf/wr-b8))


(TC.ct/defspec rwr-i16

  (prop-relative src
                 binf.gen/i16
                 2
                 binf/rr-i16
                 binf/wr-b16))


(TC.ct/defspec rwr-i32

  (prop-relative src
                 binf.gen/i32
                 4
                 binf/rr-i32
                 binf/wr-b32))


(TC.ct/defspec rwr-i64

  (prop-relative src
                 binf.gen/i64
                 8
                 binf/rr-i64
                 binf/wr-b64))


(TC.ct/defspec rwa-f32

  (prop-absolute binf.gen/f32
                 4
                 binf/ra-f32
                 binf/wa-f32
                 eq-float))


(TC.ct/defspec rwa-f64

  (prop-absolute src
                 binf.gen/f64
                 8
                 binf/ra-f64
                 binf/wa-f64
                 eq-float))


(TC.ct/defspec rwr-f32

  (prop-relative src
                 binf.gen/f32
                 4
                 binf/rr-f32
                 binf/wr-f32
                 eq-float))


(TC.ct/defspec rwr-f64

  (prop-relative src
                 binf.gen/f64
                 8
                 binf/rr-f64
                 binf/wr-f64
                 eq-float))


(def src-2

  "On the JVM, represents a native view while in JS, represents a shared buffer.
  
   Both have nothing in common but below tests can be reused across platforms."
  
  #?(:clj  (binf.native/view view-size)
     :cljs (binf.buffer/alloc-shared view-size)))


(TC.ct/defspec rwa-i8-2

  (prop-absolute src-2
                 binf.gen/i8
                 1
                 binf/ra-i8
                 binf/wa-b8))


(TC.ct/defspec rwa-i16-2

  (prop-absolute src-2
                 binf.gen/i16
                 2
                 binf/ra-i16
                 binf/wa-b16))


(TC.ct/defspec rwa-i32-2

  (prop-absolute src-2
                 binf.gen/i32
                 4
                 binf/ra-i32
                 binf/wa-b32))


(TC.ct/defspec rwa-i64-2

  (prop-absolute src-2
                 binf.gen/i64
                 8
                 binf/ra-i64
                 binf/wa-b64))


(TC.ct/defspec rwa-u8-2

  (prop-absolute src-2
                 binf.gen/u8
                 1
                 binf/ra-u8
                 binf/wa-b8))


(TC.ct/defspec rwa-u16-2

  (prop-absolute src-2
                 binf.gen/u16
                 2
                 binf/ra-u16
                 binf/wa-b16))


(TC.ct/defspec rwa-u32-2

  (prop-absolute src-2
                 binf.gen/u32
                 4
                 binf/ra-u32
                 binf/wa-b32))


(TC.ct/defspec rwa-u64-2

  (prop-absolute src-2
                 binf.gen/u64
                 8
                 binf/ra-u64
                 binf/wa-b64))


(TC.ct/defspec rwr-i8-2

  (prop-relative src-2
                 binf.gen/i8
                 1
                 binf/rr-i8
                 binf/wr-b8))


(TC.ct/defspec rwr-i16-2

  (prop-relative src-2
                 binf.gen/i16
                 2
                 binf/rr-i16
                 binf/wr-b16))


(TC.ct/defspec rwr-i32-2

  (prop-relative src-2
                 binf.gen/i32
                 4
                 binf/rr-i32
                 binf/wr-b32))


(TC.ct/defspec rwr-i64-2

  (prop-relative src-2
                 binf.gen/i64
                 8
                 binf/rr-i64
                 binf/wr-b64))


(TC.ct/defspec rwa-f32-2

  (prop-absolute src-2
                 binf.gen/f32
                 4
                 binf/ra-f32
                 binf/wa-f32
                 eq-float))


(TC.ct/defspec rwa-f64-2

  (prop-absolute src-2
                 binf.gen/f64
                 8
                 binf/ra-f64
                 binf/wa-f64
                 eq-float))


(TC.ct/defspec rwr-f32-2

  (prop-relative src-2
                 binf.gen/f32
                 4
                 binf/rr-f32
                 binf/wr-f32
                 eq-float))


(TC.ct/defspec rwr-f64-2

  (prop-relative src-2
                 binf.gen/f64
                 8
                 binf/rr-f64
                 binf/wr-f64
                 eq-float))


;;;;;;;;;; Copying from/to buffers


(defn gen-write-buffer

  ""

  [src]

  (TC.gen/let [n-byte-buffer (TC.gen/choose 0
                                            view-size)
               [position
                view]        (gen-write src
                                        n-byte-buffer)
               buffer        (binf.gen/buffer n-byte-buffer)
               n-byte-copy   (TC.gen/choose 0
                                            n-byte-buffer)
               offset        (TC.gen/choose 0
                                            (- n-byte-buffer
                                               n-byte-copy))]
    [view
     position
     buffer
     offset
     n-byte-copy]))



(defn prop-buffer

  ""

  [src w r]

  (TC.prop/for-all [[view
                     position
                     buffer
                     offset
                     n-byte]  (gen-write-buffer src)]
    (= (doall (seq buffer))
       (-> view
           (w position
              buffer
              offset
              n-byte)
           (r position
              n-byte
              buffer
              offset)
           seq))))



(defn prop-rwa-buffer

  ""

  [src]

  (prop-buffer src
               binf/wa-buffer
               binf/ra-buffer))



(defn prop-rwr-buffer

  ""

  [src]

  (prop-buffer src
               (fn write [view position buffer offset n-byte]
                 (-> view
                     (binf/seek position)
                     (binf/wr-buffer buffer
                                     offset
                                     n-byte)))
               (fn read [view position n-byte buffer offset]
                 (-> view
                     (binf/seek position)
                     (binf/rr-buffer n-byte
                                     buffer
                                     offset)))))



(TC.ct/defspec rwa-buffer

  (prop-rwa-buffer src))



(TC.ct/defspec rwr-buffer

  (prop-rwr-buffer src))



(TC.ct/defspec rwa-buffer-2

  (prop-rwa-buffer src-2))



(TC.ct/defspec rwr-buffer-2

  (prop-rwr-buffer src-2))


;;;;;;;;;; R/W strings that fit in a single view


(defn gen-string

  ""

  [src]

  (TC.gen/let [n-char (TC.gen/choose 0
                                     (Math/floor (/ view-size
                                                    4))) ;; Can accomodate any UTF-8 string within the view
               [pos
                view] (gen-write src
                                 (* 4
                                    n-char))
               string (TC.gen/fmap clojure.string/join
                                   (TC.gen/vector TC.gen/char
                                                  0
                                                  n-char))]
    [view
     pos
     string]))



(defn rr-string
  
  ""

  [view position n-byte]

  (-> view
      (binf/seek position)
      (binf/rr-string n-byte)))



(defn wr-string

  ""

  [view position string]

  (-> view
      (binf/seek position)
      (binf/wr-string string)))


(defn prop-string

  ""

  [src w r]

  (TC.prop/for-all [[view
                     position
                     string]   (gen-string src)]
    (let [[finished?
           n-byte
           n-char]   (w view
                        position
                        string)]
      (and finished?
           (= (count string)
              n-char)
           (= string
              (r view
                 position
                 n-byte))))))



(defn prop-rwa-string

  ""

  [src]

  (prop-string src
               binf/wa-string
               binf/ra-string))



(defn prop-rwr-string

  ""

  [src]

  (prop-string src
               wr-string
               rr-string))



(TC.ct/defspec rwa-string

  (prop-rwa-string src))



(TC.ct/defspec rwr-string

  (prop-rwr-string src))



(TC.ct/defspec rwa-string-2

  (prop-rwa-string src-2))



(TC.ct/defspec rwr-string-2

  (prop-rwr-string src-2))


;;;;;;;;;; R/W strings that does NOT fit into a single view


(defn gen-string-big

  ""

  [src]

  (TC.gen/let [n-byte    (TC.gen/choose 0
                                        view-size)
               [position
                view]    (gen-write src
                                    n-byte)
               string    (TC.gen/fmap clojure.string/join
                                      (let [n-char-min (inc (- (binf/limit view)
                                                               position))]
                                        (TC.gen/vector TC.gen/char
                                                       n-char-min
                                                       (+ n-char-min
                                                          (Math/ceil (/ view-size
                                                                        2))))))]
    [view
     position
     string]))



(defn prop-string-big

  ""

  [src w r]

  (TC.prop/for-all [[view
                     position
                     string] (gen-string-big src)]
    (let [[finished?
           n-byte
           #?(:clj _n-char
              :cljs n-char)
           #?(:clj char-buffer)] (w view
                                    position
                                    string)]
      (and (not finished?)
           (= string
              (str (r view
                      position
                      n-byte)
                   #?(:clj  (.toString ^CharBuffer char-buffer)
                      :cljs (.substring string
                                        n-char))))))))



(defn prop-rwa-string-big

  ""

  [src]

  (prop-string-big src
                   binf/wa-string
                   binf/ra-string))



(defn prop-rwr-string-big

  ""

  [src]

  (prop-string-big src
                   wr-string
                   rr-string))



(TC.ct/defspec rwa-string-big

  (prop-rwa-string-big src))



(TC.ct/defspec rwr-string-big

  (prop-rwr-string-big src))



(TC.ct/defspec rwa-string-big-2

  (prop-rwa-string-big src-2))



(TC.ct/defspec rwr-string-big-2

  (prop-rwr-string-big src-2))


;;;;;;;;;; Reallocating views


(TC.ct/defspec grow

  (let [view     (binf/view src)
        view-vec (vec (binf/ra-buffer view
                                      0
                                      view-size))]
    (TC.prop/for-all [endianess         gen-endianess
                      n-additional-byte (TC.gen/choose 0
                                                       view-size)
                      position          (TC.gen/choose 0
                                                       view-size)]
      (let [view-2  (binf/grow (-> view
                                   (binf/endian-set endianess)
                                   (binf/seek position))
                               n-additional-byte)
            limit-2 (binf/limit view-2)]
        (and (= (+ view-size
                   n-additional-byte)
                (binf/limit view-2))
             (= (seq (binf/ra-buffer view-2
                                     0
                                     limit-2))
                (concat view-vec
                        (repeat n-additional-byte
                                0)))
             (= position
                (binf/position view-2))
             (= endianess
                (binf/endian-get view-2)))))))


;; TODO. Ensure modifying new view does not impact old one.
;; TOOD. Alternative views.


;;;;;;;;;; Additional types / Boolean


(t/deftest bool

  (let [view (binf/view (binf.buffer/alloc 2))]

    (t/is (= true
             (-> view
                 (binf/wr-bool true)
                 (binf/seek 0)
                 binf/rr-bool))
          "Relative")

    (t/is (= true
             (-> view
                 (binf/wa-bool 1
                               true)
                 (binf/ra-bool 1)))
          "Absolute")))
