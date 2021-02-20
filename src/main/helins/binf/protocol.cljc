(ns helins.binf.protocol

  ""

  {:author "Adam Helinski"})


;;;;;;;;;;


(defprotocol IAbsoluteReader

  "Reading primitive values at an absolute position, without disturbing the current one."
  
  (ra-buffer [view position n-byte buffer offset])

  (ra-u8 [view position])

  (ra-i8 [view position])

  (ra-u16 [view position])

  (ra-i16 [view position])

  (ra-u32 [view position])

  (ra-i32 [view position])

  (ra-u64 [view position])

  (ra-i64 [view position])

  (ra-f32 [view position])

  (ra-f64 [view position])
  
  (ra-string [view decoder position n-byte]))



(defprotocol IAbsoluteWriter

  "Writing primitive values at an absolute position, without disturbing the current one.
  
   When writing integers, sign is irrelevant and truncation is automatic."
  
  (wa-buffer [view position buffer offset n-byte])

  (wa-b8 [view position int])

  (wa-b16 [view position int])

  (wa-b32 [view position int])

  (wa-b64 [view position int])

  (wa-f32 [view position floating])

  (wa-f64 [view position floating])
  
  (wa-string [view position string]))



(defprotocol IEndianess

  "Retrieving or modifying the endianess."
  
  (endian-get [view])

  (endian-set [view endianess]))



(defprotocol IRelativeReader

  "Reading primitive values from the current position, advancing it as needed. For instance,
   reading a 32-bit integer will advance the current position by 4 bytes."

  (rr-buffer [view n-byte buffer offset])

  (rr-u8 [view])

  (rr-i8 [view])

  (rr-u16 [view])

  (rr-i16 [view])

  (rr-u32 [view])

  (rr-i32 [view])

  (rr-u64 [view])

  (rr-i64 [view])

  (rr-f32 [view])

  (rr-f64 [view])
  
  (rr-string [view n-byte]
             [view decoder n-byte]))



(defprotocol IRelativeWriter

  "Writing primitive values to the current position, advancing it as needed. For instance,
   reading a 64-bit float will advance the current position by 8 bytes.

   When writing integers, sign is irrelevant and truncation is automatic."

  (wr-buffer [view buffer offset n-byte])
  
  (wr-b8 [view int])

  (wr-b16 [view int])

  (wr-b32 [view int])

  (wr-b64 [view int])

  (wr-f32 [view floating])

  (wr-f64 [view floating])

  (wr-string [view string]))



(defprotocol IPosition

  "Handling the position of a view and its relation to its buffer."

  (offset [view])

  (position [view])

  (seek [view position])
  
  (skip [view n-byte])

  (to-buffer [view]))



(defprotocol IViewable

  "Building a new view."

  (view [viewable]
        [viewable offset]
        [viewable offset n-byte]))
