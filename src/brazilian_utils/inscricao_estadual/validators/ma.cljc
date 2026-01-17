(ns brazilian-utils.inscricao-estadual.validators.ma
  (:require [brazilian-utils.helpers :as helpers]))

;; Maranhão (MA): 9 digits
;; Format: 12XXXXXX-X (must start with 12)
;; Uses modulo 11 with weights [9,8,7,6,5,4,3,2], if dig >= 10 then dig = 0

(defn is-valid? [ie-str]
  (let [ie (helpers/only-numbers ie-str)]
    (if (and (= 9 (count ie))
             (= "12" (subs ie 0 2)))  ; Must start with 12
      (let [body (subs ie 0 8)
            check-digit (helpers/char->digit (get ie 8))
            weights [9 8 7 6 5 4 3 2]
            sum (helpers/weighted-sum body weights)
            remainder (mod sum 11)
            dig (- 11 remainder)
            final-dig (if (>= dig 10) 0 dig)]
        (= check-digit final-dig))
      false)))
