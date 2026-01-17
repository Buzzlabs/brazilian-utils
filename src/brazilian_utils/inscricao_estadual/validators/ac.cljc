(ns brazilian-utils.inscricao-estadual.validators.ac
  (:require [brazilian-utils.helpers :as helpers]))

;; Acre (AC): 13 digits
;; Format: 01XXXXXXXXX-XX (must start with 01, then 9 body digits + 2 check digits)
;; Uses modulo 11 with decreasing weights that wrap from 1 to 9

(defn- calc-digit 
  "Calculate check digit with weights starting at (length - 7) and wrapping 1->9"
  [body]
  (let [start-weight (- (count body) 7)
        digits (helpers/digits->ints body)]
    (loop [idx 0
           weight start-weight
           sum 0]
      (if (= idx (count digits))
        (let [remainder (mod sum 11)
              result (- 11 remainder)]
          (if (>= result 10) 0 result))
        (let [nw (dec weight)]
          (recur (inc idx)
                 (if (= nw 1) 9 nw)
                 (+ sum (* (nth digits idx) weight))))))))

(defn is-valid? [ie-str]
  (let [ie (helpers/only-numbers ie-str)]
    (if (and (= 13 (count ie))
             (= "01" (subs ie 0 2)))  ; Must start with 01
      (let [body (subs ie 0 11)
            check1 (helpers/char->digit (get ie 11))
            check2 (helpers/char->digit (get ie 12))
            calculated-check1 (calc-digit body)
            body-with-check1 (str body calculated-check1)
            calculated-check2 (calc-digit body-with-check1)]
        (and (= check1 calculated-check1)
             (= check2 calculated-check2)))
      false)))
