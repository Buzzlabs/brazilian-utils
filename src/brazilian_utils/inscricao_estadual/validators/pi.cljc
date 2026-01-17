(ns brazilian-utils.inscricao-estadual.validators.pi
  (:require [brazilian-utils.helpers :as helpers]))

;; Piauí (PI): 9 digits
;; Format: XXXXXXXXX (8 body digits + 1 check digit)
;; Uses modulo 11 with weights [9,8,7,6,5,4,3,2] (same as CE)

(defn is-valid? [ie-str]
  (let [ie (helpers/only-numbers ie-str)]
    (if (= 9 (count ie))
      (let [body (subs ie 0 8)
            check-digit (helpers/char->digit (get ie 8))
            calculated-digit (helpers/check-digit body [9 8 7 6 5 4 3 2])]
        (= check-digit calculated-digit))
      false)))
