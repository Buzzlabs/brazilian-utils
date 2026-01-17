(ns brazilian-utils.inscricao-estadual.validators.ba
  "Bahia (BA) - 8 ou 9 dígitos, dois dígitos verificadores.
   Regra do módulo:
   - IE com 8 dígitos: verifica conforme primeiro dígito
   - IE com 9 dígitos: verifica conforme primeiro dígito (posição 1)
   - Se primeiro dígito ∈ {0,1,2,3,4,5,8} usa módulo 10; caso contrário usa módulo 11"
  (:require [brazilian-utils.helpers :as helpers]))

(defn- calc-digit [body modulus]
  (let [len (count body)
        weights (range (inc len) 1 -1)
        sum (helpers/weighted-sum body weights)
        rest (mod sum modulus)
        dig (- modulus rest)]
    (if (>= dig 10) 0 dig)))

(defn- choose-mod [ie]
  (let [len (count ie)
        pos (if (= len 9) 1 0)
        first-digit (helpers/char->digit (get ie pos))
        mod10-set #{0 1 2 3 4 5 8}]
    (if (contains? mod10-set first-digit) 10 11)))

(defn is-valid?
  [ie]
  (let [len (count ie)]
    (if (or (= len 8) (= len 9))
      (let [modulus (choose-mod ie)
            body (subs ie 0 (- len 2))
            second (calc-digit body modulus)
            first  (calc-digit (str body second) modulus)
            d1 (helpers/char->digit (get ie (- len 2)))
            d2 (helpers/char->digit (get ie (dec len)))]
        (and (= first d1) (= second d2)))
      false)))