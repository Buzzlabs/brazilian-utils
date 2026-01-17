(ns brazilian-utils.inscricao-estadual.validators.mt
  (:require [brazilian-utils.helpers :as helpers]))

;; Mato Grosso (MT): 11 digits
;; Format: XXXXXXXXXXX (10 body digits + 1 check digit)
;; Uses modulo 11 with weights starting at 3, decrementing, wrapping from 1 to 9

(defn- generate-weights
  "Generate weights for MT: start at 3, decrement, wrap from 1 to 9"
  [length]
  (loop [weight 3
         weights []]
    (if (= (count weights) length)
      weights
      (recur (let [nw (dec weight)]
               (if (= nw 1) 9 nw))
             (conj weights weight)))))

(defn is-valid? [ie-str]
  (let [ie (helpers/only-numbers ie-str)]
    (if (= 11 (count ie))
      (let [body (subs ie 0 10)
            check-digit (helpers/char->digit (get ie 10))
            weights (generate-weights 10)
            sum (helpers/weighted-sum body weights)
            remainder (mod sum 11)
            dig (- 11 remainder)
            final-dig (if (>= dig 10) 0 dig)]
        (= check-digit final-dig))
      false)))

(defn validate
  "Validates MT IE with detailed error messages.
   Returns {:valid? true} or {:valid? false :error \"message\"}"
  [ie-str]
  (let [ie (helpers/only-numbers ie-str)]
    (cond
      (not= 11 (count ie))
      {:valid? false, :error "IE must have exactly 11 digits"}
      
      (not (is-valid? ie-str))
      {:valid? false, :error "Invalid check digit"}
      
      :else
      {:valid? true})))
