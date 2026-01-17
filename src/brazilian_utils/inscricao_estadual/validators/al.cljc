(ns brazilian-utils.inscricao-estadual.validators.al
  (:require [brazilian-utils.helpers :as helpers]))

;; Alagoas (AL): 9 digits
;; Format: 24XXXXXX-X (must start with 24, then 6 body digits + 1 check digit)
;; Uses special formula: product = sum * 10, digit = product - floor(product/11)*11

(defn is-valid? [ie-str]
  (let [ie (helpers/only-numbers ie-str)]
    (if (and (= 9 (count ie))
             (= "24" (subs ie 0 2)))  ; Must start with 24
      (let [body (subs ie 0 8)
            check-digit (helpers/char->digit (get ie 8))
            weights [9 8 7 6 5 4 3 2]
            sum (helpers/weighted-sum body weights)
            product (* sum 10)
            calculated-digit (- product (* (quot product 11) 11))]
        (= check-digit (if (>= calculated-digit 10) 0 calculated-digit)))
      false)))
