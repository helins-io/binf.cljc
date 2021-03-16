;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at https://mozilla.org/MPL/2.0/.


(ns helins.binf

  "Core namespace for handling views.
  
   See README for an overview."

  {:author "Adam Helins"}

  (:require [helins.binf.buffer         :as binf.buffer]
            [helins.binf.protocol       :as binf.protocol]
            [helins.binf.protocol.impl]
            [helins.binf.string         :as binf.string]))


(declare remaining)


;;;;;;;;;; Pointing to protocol implementations


;;;;; IAbsoluteReader


(defn ra-buffer

  "Reads `n-byte` bytes from an absolute `position` and returns them in a new buffer or in the
   given `buffer` at the specified `offset` (defaults to 0)."


  ([this position n-byte]

   (binf.protocol/ra-buffer this
                            position
                            n-byte
                            (binf.buffer/alloc n-byte)
                            0))


  ([this position n-byte buffer]

   (binf.protocol/ra-buffer this
                            position
                            n-byte
                            buffer
                            0))


  ([view position n-byte buffer offset]

   (binf.protocol/ra-buffer view
                            position
                            n-byte
                            buffer
                            offset)))



(defn ra-u8
  
  "Reads an unsigned 8-bit integer from an absolute `position`."

  [view position]

  (binf.protocol/ra-u8 view
                       position))



(defn ra-i8
  
  "Reads a signed 8-bit integer from an absolute `position`."

  [view position]

  (binf.protocol/ra-i8 view
                       position))



(defn ra-u16

  "Reads an unsigned 16-bit integer from an absolute `position`."

  [view position]

  (binf.protocol/ra-u16 view
                        position))



(defn ra-i16

  "Reads a signed 16-bit integer from an absolute `position`."

  [view position]

  (binf.protocol/ra-i16 view
                        position))



(defn ra-u32

  "Reads an unsigned 32-bit integer from an absolute `position`."

  [view position]

  (binf.protocol/ra-u32 view
                        position))



(defn ra-i32

  "Reads a signed 32-bit integer from an absolute `position`."

  [view position]

  (binf.protocol/ra-i32 view
                        position))


(defn ra-u64

  "Reads an unsigned 64-bit integer from an absolute `position`."

  [view position]

  (binf.protocol/ra-u64 view
                        position))



(defn ra-i64

  "Reads a signed 64-bit integer from an absolute `position`."

  [view position]

  (binf.protocol/ra-i64 view
                        position))



(defn ra-f32

  "Reads a 32-bit float from an absolute `position`."

  [view position]

  (binf.protocol/ra-f32 view
                        position))



(defn ra-f64

  "Reads a 64-bit float from an absolute `position`."

  [view position]

  (binf.protocol/ra-f64 view
                        position))



(defn ra-string

  "Reads a string consisting of `n-byte` bytes from an absolute `position`.
  
   A string decoder may be provided (default is UTF-8).
  
   See [[helins.binf.string/decoder]]."


  ([view position n-byte]

   (binf.protocol/ra-string view
                            binf.string/decoder-utf-8
                            position
                            n-byte))


  ([view decoder position n-byte]

   (binf.protocol/ra-string view
                            decoder
                            position
                            n-byte)))


;;;;; IAbsoluteWriter


(defn wa-buffer

  "Copies the given `buffer` to an absolute `position`.
  
   An `offset` in the buffer as well as a number of bytes to copy (`n-byte`) may be provided."


  ([view position buffer]

    (binf.protocol/wa-buffer view
                             position
                             buffer
                             0
                             (count buffer)))


  ([view position buffer offset]

   (binf.protocol/wa-buffer view
                            position
                            buffer
                            offset
                            (- (count buffer)
                               offset)))


  ([view position buffer offset n-byte]

   (binf.protocol/wa-buffer view
                            position
                            buffer
                            offset
                            n-byte)))



(defn wa-b8

  "Writes an 8-bit integer to an absolute position."

  [view position int]

  (binf.protocol/wa-b8 view
                       position
                       int))



(defn wa-b16

  "Writes a 16-bit integer to an absolute `position`."

  [view position int]

  (binf.protocol/wa-b16 view
                        position
                        int))



(defn wa-b32

  "Writes a 32-bit integer to an absolute `position`."

  [view position int]

  (binf.protocol/wa-b32 view
                        position
                        int))



(defn wa-b64

  "Writes a 64-bit integer to an absolute `position`."

  [view position int64]

  (binf.protocol/wa-b64 view
                        position
                        int64))



(defn wa-f32

  "Writes a 32-bit float to an absolute `position`."

  [view position floating]

  (binf.protocol/wa-f32 view
                        position
                        floating))



(defn wa-f64

  "Writes a 64-bit float to an absolute `position`."

  [view position floating]

  (binf.protocol/wa-f64 view
                        position
                        floating))



(defn wa-string
  
  "Writes a string (encoded as UTF-8) to an absolute `position`.

   Unlike other  `wa-*` functions which are implemented as a fluent interface, this function returns
   a tuple indicating how many bytes and chars have been written, and if the process is finished:
   `[finished? n-byte n-chars]`.
  
   With that information, the user can continue writing if needed. On the JVM, the tuple contains a 4th
   item which is a `CharBuffer` containing the rest of the unwritten string which can be passed in place
   of the `string` argument."

  [view position string]

  (binf.protocol/wa-string view
                           position
                           string))


;;;;; IBackingBuffer


(defn backing-buffer

  "Returns the buffer of those views which do wrap one, nil otherwise.

   It is possible to implement some protocols of this library over something that does not have a backing buffer.
   Actually, on the JVM, a `DirectByteBuffer` is an example of a supported view which operates over native memory
   instead of a Java byte array (ie. instead of a buffer in the context of this library)."

  [view]

  (binf.protocol/backing-buffer view))



(defn buffer-offset

  "Returns the offset relative to the backing buffer, nil if there is not any.

   See [[backing-buffer]] for background."

  [view]

  (binf.protocol/buffer-offset view))


;;;;; IEndianess


(defn endian-get

  "Returns the endianess of the given `view`, either `:big-endian` or `:little-endian`."

  [view]

  (binf.protocol/endian-get view))



(defn endian-set

  "Sets the endianess of the given `view`, either `:big-endian` or `:little-endian`."

  [view endianess]

  (binf.protocol/endian-set view
                            endianess))


;;;;; IGrow


(defn grow

  "Returns a new view with `n-additional-byte` added, copying entirely the given `view`.
  
   It is particularly useful when writing data of yet unknown length, giving the ability of
   growing as needed."

  [view n-additional-byte]

  (binf.protocol/grow view
                      n-additional-byte))


;;;;; IRelativeReader


(defn rr-buffer

  "Reads `n-byte` and returns them in a new buffer or in the given one at the specified `offset`
   (defaults to 0)."


  ([view n-byte]

   (binf.protocol/rr-buffer view
                            n-byte
                            (binf.buffer/alloc n-byte)
                            0))


  ([view n-byte buffer]

   (binf.protocol/rr-buffer view
                            n-byte
                            buffer
                            0))


  ([view n-byte buffer offset]

   (binf.protocol/rr-buffer view
                            n-byte
                            buffer
                            offset)))



(defn rr-u8

  "Reads an unsigned 8-bit integer from the current position."

  [view]

  (binf.protocol/rr-u8 view))



(defn rr-i8

  "Reads a signed 8-bit integer from the current position."

  [view]

  (binf.protocol/rr-i8 view))



(defn rr-u16

  "Reads an unsigned 16-bit integer from the current position."

  [view]

  (binf.protocol/rr-u16 view))



(defn rr-i16

  "Reads a signed 16-bit integer from the current position."

  [view]

  (binf.protocol/rr-i16 view))



(defn rr-u32

  "Reads an unsigned 32-bit integer from the current position."

  [view]

  (binf.protocol/rr-u32 view))



(defn rr-i32

  "Reads a signed 32-bit integer from the current position."

  [view]

  (binf.protocol/rr-i32 view))



(defn rr-u64

  "Reads an unsigned 64-bit integer from the current position."

  [view]

  (binf.protocol/rr-u64 view))



(defn rr-i64

  "Reads a signed 64-bit integer from the current position."

  [view]

  (binf.protocol/rr-i64 view))



(defn rr-f32

  "Reads a 32-bit float from the current position."

  [view]

  (binf.protocol/rr-f32 view))



(defn rr-f64

  "Reads a 64-bit float from the current position."

  [view]

  (binf.protocol/rr-f64 view))



(defn rr-string
  
  "Reads a string consisting of `n-byte` bytes from the current position.

   A non-nil decoder may be provided (default is UTF-8).
  
   See [[helins.binf.string/decoder]]."


  ([view n-byte]

   (binf.protocol/rr-string view
                            binf.string/decoder-utf-8
                            n-byte))


  ([view decoder n-byte]

   (binf.protocol/rr-string view
                            decoder
                            n-byte)))


;;;;; IRelativeWriter


(defn wr-buffer
  
  "Copies the given `buffer` to the current position.

   An `offset` in the buffer as well as a number of bytes to copy (`n-byte`) may be provided."

  ([view buffer]

   (binf.protocol/wr-buffer view
                            buffer
                            0
                            (count buffer)))


  ([view buffer offset]

   (binf.protocol/wr-buffer view
                            buffer
                            offset
                            (- (count buffer)
                               offset)))


  ([view buffer offset n-byte]

   (binf.protocol/wr-buffer view
                            buffer
                            offset
                            n-byte)))



(defn wr-b8

  "Writes an 8-bit integer to the current position."

  [view int]

  (binf.protocol/wr-b8 view
                       int))



(defn wr-b16

  "Writes a 16-bit integer to the current position."

  [view int]

  (binf.protocol/wr-b16 view
                        int))



(defn wr-b32

  "Writes a 32-bit integer to the current position."

  [view int]

  (binf.protocol/wr-b32 view
                        int))



(defn wr-b64

  "Writes a 64-bit integer to the current position."

  [view int]

  (binf.protocol/wr-b64 view
                        int))



(defn wr-f32

  "Writes a 32-bit float to the current position."

  [view floating]

  (binf.protocol/wr-f32 view
                        floating))



(defn wr-f64

  "Writes a 64-bit float to the current position."

  [view floating]

  (binf.protocol/wr-f64 view
                        floating))



(defn wr-string
  
  "Writes a string to the current position, encoded at UTF-8.
  
   Cf. [[wa-string]] about the returned value"

  [view string]

  (binf.protocol/wr-string view
                           string))


;;;; IPosition


(defn limit

  "Returns the number of bytes a view contains."

  [view]

  (binf.protocol/limit view))



(defn position

  "Returns the current position."

  [view]

  (binf.protocol/position view))



(defn seek

  "Modifies the current position."

  [view position]

  (binf.protocol/seek view
                      position))



(defn skip

  "Advances the current position by `n-byte` bytes."

  [view n-byte]

  (binf.protocol/skip view
                      n-byte))


;;;;; IViewable


(defn view
  
  "A view can be created from a buffer (see [[helins.binf.buffer/alloc]]) or from another view.
  
   An `offset` as well as a size (`n-byte`) may be provided.
  
   ```clojure
   (def my-buffer
        (binf.buffer/alloc 100))

   ;; View with an offset of 50 bytes, 40 bytes long
   (def my-view
        (binf/view my-buffer
                   50
                   40))

   ;; View from a view, offset of 60 bytes in the original buffer (50 + 10), 20 bytes long
   (def inner-view
        (binf/view my-view
                   10
                   20))
   ```"

  ([viewable]

   (binf.protocol/view viewable))


  ([viewable offset]

   (binf.protocol/view viewable
                       offset))


  ([viewable offset n-byte]

   (binf.protocol/view viewable
                       offset
                       n-byte)))


;;;;;;;;;; Helper functions


(defn garanteed?

  "Returns true if the `view` can consume at least `n-byte` from the current position."

  [view n-byte]

  (>= (remaining view)
      n-byte))



(defn remaining

  "Returns the number of bytes remaining until the end of the view given the current position."

  [view]

  (- (binf.protocol/limit view)
     (binf.protocol/position view)))


;;;;;;;;;; Additional types / Boolean


(defn ra-bool

  "Reads a boolean value from an absolute `position`."

  [view position]

  (= (ra-u8 view
            position)
     1))



(defn rr-bool

  "Reads a boolean value from the current position."

  [view]

  (= (rr-u8 view)
     1))



(defn wa-bool

  "Writes a boolean value to an absolute `position`."

  [view position true?]

  (wa-b8 view
         position
         (if true?
           1
           0)))



(defn wr-bool

  "Writes a boolean value from the current position."

  [view true?]

  (wr-b8 view
         (if true?
           1
           0)))
