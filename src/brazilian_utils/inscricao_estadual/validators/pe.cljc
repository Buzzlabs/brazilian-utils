(ns brazilian-utils.inscricao-estadual.validators.pe
  (:require [brazilian-utils.helpers :as helpers]))

;; Pernambuco (PE): 9 digits
;; Format: XXXXXXX-XX (7 body digits + 2 check digits)
;; Uses modulo 11 with weights starting at (length + 1) and decrementing

(defn- calc-digit
  "Calculate check digit with weights starting at (length + 1) and decrementing"
  [body]
  (let [start-weight (inc (count body))
        weights (range start-weight 1 -1)
        sum (helpers/weighted-sum body weights)
        remainder (mod sum 11)
        result (- 11 remainder)]
    (if (>= result 10) 0 result)))

(defn is-valid? [ie-str]
  (let [ie (helpers/only-numbers ie-str)]
    (if (= 9 (count ie))
      (let [body (subs ie 0 7)
            check1 (helpers/char->digit (get ie 7))
            check2 (helpers/char->digit (get ie 8))
            calculated-check1 (calc-digit body)
            body-with-check1 (str body calculated-check1)
            calculated-check2 (calc-digit body-with-check1)]
        (and (= check1 calculated-check1)
             (= check2 calculated-check2)))
      false)))
