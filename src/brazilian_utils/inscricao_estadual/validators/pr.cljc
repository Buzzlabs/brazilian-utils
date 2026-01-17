(ns brazilian-utils.inscricao-estadual.validators.pr
  (:require [brazilian-utils.helpers :as helpers]))

;; Paraná (PR): 10 digits
;; Format: XXXXXXXX-XX (8 body digits + 2 check digits)
;; Uses modulo 11 with weights starting at (length - 5), decrementing, wrapping from 1 to 7

(defn- calc-digit
  "Calculate check digit with weights starting at (length - 5) and wrapping 1->7"
  [body]
  (let [start-weight (- (count body) 5)
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
                 (if (= nw 1) 7 nw)
                 (+ sum (* (nth digits idx) weight))))))))

(defn is-valid? [ie-str]
  (let [ie (helpers/only-numbers ie-str)]
    (if (= 10 (count ie))
      (let [body (subs ie 0 8)
            check1 (helpers/char->digit (get ie 8))
            check2 (helpers/char->digit (get ie 9))
            calculated-check1 (calc-digit body)
            body-with-check1 (str body calculated-check1)
            calculated-check2 (calc-digit body-with-check1)]
        (and (= check1 calculated-check1)
             (= check2 calculated-check2)))
      false)))
