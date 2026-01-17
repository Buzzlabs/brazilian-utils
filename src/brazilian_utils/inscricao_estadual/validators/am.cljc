(ns brazilian-utils.inscricao-estadual.validators.am
  (:require [brazilian-utils.helpers :as helpers]))

;; Amazonas (AM): 9 digits
;; Format: XXXXXXXXX (8 body digits + 1 check digit)
;; Special rule: if sum < 11, dig = 11 - sum; else dig = 11 - (sum % 11), if dig >= 10 then dig = 0

(defn is-valid? [ie-str]
  (let [ie (helpers/only-numbers ie-str)]
    (if (= 9 (count ie))
      (let [body (subs ie 0 8)
            check-digit (helpers/char->digit (get ie 8))
            weights [9 8 7 6 5 4 3 2]
            sum (helpers/weighted-sum body weights)
            
            ; Special AM rule
            dig (if (< sum 11)
                  (- 11 sum)
                  (let [remainder (mod sum 11)
                        d (- 11 remainder)]
                    (if (>= d 10) 0 d)))]
        (= check-digit dig))
      false)))
