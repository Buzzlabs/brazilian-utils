(ns brazilian-utils.pis.internal
  (:require [brazilian-utils.helpers :as helpers]))

(def ^:const pis-length 11)

;; PIS check digit calculation weights
(def ^:const check-digit-weights [3 2 9 8 7 6 5 4 3 2])

(defn calc-check-digit*
  "Computes the PIS check digit using weighted sum.

   PIS algorithm: digit = 11 - (weightedSum % 11);
   if result is 10 or 11, digit becomes 0."
  [base]
  (let [d (helpers/check-digit base check-digit-weights {:stringify? true})
        d-val (helpers/parse-int d)]  
    (if (>= d-val 10) "0" d)))

(defn valid-checksum*
  "Validates the PIS check digit for 11-character cleaned input."
  [pis]
  (when (= (count pis) pis-length)
    (let [base10 (subs pis 0 10)
          expected-dv (subs pis 10 11)
          calculated-dv (calc-check-digit* base10)]
      (= expected-dv calculated-dv))))
