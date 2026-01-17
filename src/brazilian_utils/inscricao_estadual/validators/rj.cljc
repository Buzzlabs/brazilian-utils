(ns brazilian-utils.inscricao-estadual.validators.rj
  "Rio de Janeiro (RJ) - 8 dígitos"
  (:require [brazilian-utils.helpers :as helpers]))

;; Rio de Janeiro (RJ): 8 digits
;; Format: XXXXXXX-X (7 body digits + 1 check digit)
;; Uses modulo 11 with weights starting at 2, decrementing, wrapping from 1 to 7

(defn- generate-weights
  "Generate weights for RJ: start at 2, decrement, wrap from 1 to 7"
  [length]
  (loop [weight 2
         weights []]
    (if (= (count weights) length)
      weights
      (recur (let [nw (dec weight)]
               (if (= nw 1) 7 nw))
             (conj weights weight)))))

(defn is-valid?
  [ie]
  (if (= 8 (count ie))
    (let [body (subs ie 0 7)
          dv (helpers/char->digit (get ie 7))
          weights (generate-weights 7)
          sum (helpers/weighted-sum body weights)
          remainder (mod sum 11)
          dig (- 11 remainder)
          final-dig (if (>= dig 10) 0 dig)]
      (= dv final-dig))
    false))