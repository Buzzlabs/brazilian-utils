(ns brazilian-utils.cpf.internal
  (:require [brazilian-utils.helpers :as helpers]))

(def ^:const cpf-length 11)
(def ^:const first-check-digit-weight 10)
(def ^:const second-check-digit-weight 11)

(defn calc-check-digit*
  "Computes mod-11 check digit for a CPF base using given weight."
  [base weight]
  (helpers/mod11-check-digit base weight {:stringify? true}))

(defn valid-checksum*
  "Validates both CPF check digits for 11-character cleaned input."
  [cpf]
  (when (= (count cpf) cpf-length)
    (let [base9 (subs cpf 0 9)
          dv1 (calc-check-digit* base9 first-check-digit-weight)
          dv2 (calc-check-digit* (str base9 dv1) second-check-digit-weight)]
      (and (= (subs cpf 9 10) dv1)
           (= (subs cpf 10 11) dv2)))))