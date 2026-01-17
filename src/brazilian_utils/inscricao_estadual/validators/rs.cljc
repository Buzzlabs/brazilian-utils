(ns brazilian-utils.inscricao-estadual.validators.rs
  (:require [brazilian-utils.helpers :as helpers]))

;; Rio Grande do Sul (RS): 10 digits
;; Format: XXXXXXXXX-X (9 body digits + 1 check digit)
;; Uses modulo 11 with weights starting at 2, decrementing, wrapping from 1 to 9

(defn- generate-weights
  "Generate weights for RS: start at 2, decrement, wrap from 1 to 9"
  [length]
  (loop [weight 2
         weights []]
    (if (= (count weights) length)
      weights
      (recur (let [nw (dec weight)]
               (if (= nw 1) 9 nw))
             (conj weights weight)))))

(defn is-valid? [ie-str]
  (let [ie (helpers/only-numbers ie-str)]
    (if (= 10 (count ie))
      (let [body (subs ie 0 9)
            check-digit (helpers/char->digit (get ie 9))
            weights (generate-weights 9)
            sum (helpers/weighted-sum body weights)
            remainder (mod sum 11)
            dig (- 11 remainder)
            final-dig (if (>= dig 10) 0 dig)]
        (= check-digit final-dig))
      false)))
