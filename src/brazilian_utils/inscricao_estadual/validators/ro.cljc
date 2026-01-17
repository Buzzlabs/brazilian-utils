(ns brazilian-utils.inscricao-estadual.validators.ro
  (:require [brazilian-utils.helpers :as helpers]))

;; Rondônia (RO): 14 digits
;; Format: XXXXXXXXXXXXX-X (13 body digits + 1 check digit)
;; Uses modulo 11 with weights starting at 6, decrementing, wrapping from 1 to 9
;; Special rule: if dig >= 10, dig -= 10

(defn- generate-weights
  "Generate weights for RO: start at 6, decrement, wrap from 1 to 9"
  [length]
  (loop [weight 6
         weights []]
    (if (= (count weights) length)
      weights
      (recur (let [nw (dec weight)]
               (if (= nw 1) 9 nw))
             (conj weights weight)))))

(defn is-valid? [ie-str]
  (let [ie (helpers/only-numbers ie-str)]
    (if (= 14 (count ie))
      (let [body (subs ie 0 13)
            check-digit (helpers/char->digit (get ie 13))
            weights (generate-weights 13)
            sum (helpers/weighted-sum body weights)
            remainder (mod sum 11)
            dig (- 11 remainder)
            ; Special RO rule: if dig >= 10, subtract 10
            final-dig (if (>= dig 10) (- dig 10) dig)]
        (= check-digit final-dig))
      false)))
