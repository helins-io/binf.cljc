(ns helins.binf.dev

  "For daydreaming in the repl."

  (:require [helins.binf               :as binf]
            [helins.binf.base64        :as binf.base64]
            [helins.binf.buffer        :as binf.buffer]
            [helins.binf.endian        :as binf.endian]
            [helins.binf.float         :as binf.float]
            [helins.binf.int           :as binf.int]
            [helins.binf.int64         :as binf.int64]
            [helins.binf.protocol      :as binf.protocol]
            [helins.binf.protocol.impl :as binf.protocol.impl]
            [helins.binf.string        :as binf.string]))


;;;;;;;;;;


(comment


  (.grow js/mem
         1)

  (.-byteLength (.-buffer js/mem))

  (set! js/mem
        (js/WebAssembly.Memory. #js {"initial" 1}))


  (set! js/v
        (binf/view (.-buffer js/mem)))



  
  (def v
       (binf/view (binf/buffer-shared 32)))


  (binf/wa-b16 v
               0
               4)

  (binf/ra-u16 v
               0)


  (binf/ra-buffer v
                  0
                  10)

  (binf/wa-buffer v
                  2
                  (binf/to-buffer v)
                  0
                  4)

  (binf/wa-string v
                  0
                  "coucou")



  )
