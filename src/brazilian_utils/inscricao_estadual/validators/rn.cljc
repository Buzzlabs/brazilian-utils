(ns brazilian-utils.inscricao-estadual.validators.rn
  (:require [brazilian-utils.helpers :as helpers]))

;; Rio Grande do Norte (RN): 9 or 10 digits
;; Format: 20XXXXXXX-X or 20XXXXXXXX-X (must start with 20)
;; Uses modulo 11 with weights starting at length and decrementing

(defn is-valid? [ie-str]
  (let [ie (helpers/only-numbers ie-str)
        len (count ie)]
    (if (and (or (= len 9) (= len 10))
             (= "20" (subs ie 0 2)))  ; Must start with 20
      (let [body (subs ie 0 (dec len))
            check-digit (helpers/char->digit (get ie (dec len)))
            weights (range len 1 -1)
            sum (helpers/weighted-sum body weights)
            remainder (mod sum 11)
            dig (- 11 remainder)
            final-dig (if (>= dig 10) 0 dig)]
        (= check-digit final-dig))
      false)))
