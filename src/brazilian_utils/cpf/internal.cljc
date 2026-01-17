(ns brazilian-utils.cpf.internal
  (:require [brazilian-utils.helpers :as helpers]
            [brazilian-utils.states :as states]))

(def ^:const cpf-length 11)
(def ^:const first-check-digit-weight 10)
(def ^:const second-check-digit-weight 11)

(defn calc-check-digit*
  "Computes mod-11 check digit for a CPF base using given weight."
  [base weight]
  (helpers/check-digit base weight {:stringify? true}))

(defn valid-checksum*
  "Validates both CPF check digits for 11-character cleaned input."
  [cpf]
  (when (= (count cpf) cpf-length)
    (let [base9 (subs cpf 0 9)
          dv1 (calc-check-digit* base9 first-check-digit-weight)
          dv2 (calc-check-digit* (str base9 dv1) second-check-digit-weight)]
      (and (= (subs cpf 9 10) dv1)
           (= (subs cpf 10 11) dv2)))))

(defn get-state-code
  "Returns the numeric state code for a UF keyword, or a random digit if invalid/nil.
  
  Args:
    state - Optional state keyword (e.g., :SP, :RJ)
    
  Returns:
    Integer from 0-9"
  [state]
  (or (some-> state states/uf->code helpers/parse-int)
      (rand-int 10)))

(defn generate-non-repeated-base
  "Generates a random 8-digit base that won't be all repeated when combined with state-code.
  
  Args:
    state-code - The state digit (0-9)
    
  Returns:
    String with 8 random digits"
  [state-code]
  (loop []
    (let [candidate (helpers/random-digits 8)
          full-base (str candidate state-code)]
      (if (helpers/repeated-digits? full-base)
        (recur)
        candidate))))
