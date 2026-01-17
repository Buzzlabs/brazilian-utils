(ns brazilian-utils.inscricao-estadual.validators.sp
  "São Paulo (SP) - 12 dígitos
   Position 9: first DV (weights [1,3,4,5,6,7,8,10])
   Position 12: second DV (weights start at 3, decrement, wrap from 1 to 10)"
  (:require [brazilian-utils.helpers :as helpers]))

(defn- calc-second-digit-weights
  "Generate weights for second digit: start at 3, decrement, wrap from 1 to 10"
  [length]
  (loop [weight 3
         weights []]
    (if (= (count weights) length)
      weights
      (recur (let [nw (dec weight)]
               (if (= nw 1) 10 nw))
             (conj weights weight)))))

(defn is-valid?
  [ie]
  (if (= 12 (count ie))
    (let [body (subs ie 0 8)
          pos9-dv (helpers/char->digit (get ie 8))
          full-11 (subs ie 0 11)
          pos12-dv (helpers/char->digit (get ie 11))

          ; Calculate DV at position 9
          weights1 [1 3 4 5 6 7 8 10]
          sum1 (helpers/weighted-sum body weights1)
          remainder1 (mod sum1 11)
          calc-dv1 (mod remainder1 10)

          ; Calculate DV at position 12
          weights2 (calc-second-digit-weights 11)
          sum2 (helpers/weighted-sum full-11 weights2)
          remainder2 (mod sum2 11)
          calc-dv2 (mod remainder2 10)]
      (and (= calc-dv1 pos9-dv) (= calc-dv2 pos12-dv)))
    false))