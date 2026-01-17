(ns brazilian-utils.inscricao-estadual.validators.rr
  "Roraima (RR) - 9 dígitos. Must start with '24'.
   Special algorithm: DV = (sum of digits * growing weights) % 9"
  (:require [brazilian-utils.helpers :as helpers]))

(defn is-valid?
  "Validates Roraima IE (9 digits).
   Must start with '24' and have valid check digit."
  [ie-str]
  (let [ie (helpers/only-numbers ie-str)]
    (if (= 9 (count ie))
      (and
       ;; Must start with "24"
       (= "24" (subs ie 0 2))
       ;; Calculate check digit
       (let [body (subs ie 0 8)
             check-digit (helpers/char->digit (get ie 8))
             ;; Growing weights: 1, 2, 3, 4, 5, 6, 7, 8
             weights (range 1 9)
             sum (helpers/weighted-sum body weights)
             calculated-digit (mod sum 9)]
         (= check-digit calculated-digit)))
      false)))