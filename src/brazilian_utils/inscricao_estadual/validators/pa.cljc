(ns brazilian-utils.inscricao-estadual.validators.pa
  (:require [brazilian-utils.helpers :as helpers]))

;; Pará (PA): 9 digits
;; Format: 15XXXXXX-X (must start with 15, then 6 body digits + 1 check digit)
;; Uses modulo 11 with weights [9,8,7,6,5,4,3,2]

(defn is-valid? [ie-str]
  (let [ie (helpers/only-numbers ie-str)]
    (if (and (= 9 (count ie))
             (= "15" (subs ie 0 2)))  ; Must start with 15
      (let [body (subs ie 0 8)
            check-digit (helpers/char->digit (get ie 8))
            calculated-digit (helpers/check-digit body [9 8 7 6 5 4 3 2])]
        (= check-digit calculated-digit))
      false)))
