(ns brazilian-utils.processo-juridico.internal
  (:require [brazilian-utils.helpers :as helpers]))

(def ^:const processo-length
  "Standard length of a Brazilian legal process number (processo jurídico).
   Format: NNNNNNN-DD.AAAA.J.TT.OOOO (20 digits total)"
  20)

(defn verify-digit*
  "Verifies check digits using MOD 97-10 algorithm.
   
   The MOD 97-10 algorithm rearranges the processo number and verifies
   that the modulo 97 equals 1, ensuring check digit validity.
   
   Original format: NNNNNNN-DD.AAAA.J.TT.OOOO
   Positions:       0-6    7-8  9-12 13 14-15 16-19
   
   Args:
     processo - 20-digit processo string
   
   Returns:
     true if check digits are valid, false otherwise"
  [processo]
  (when (= (count processo) processo-length)
    (let [;;   NNNNNNN = sequential number (positions 0-6)
          sequential (subs processo 0 7)
          ;;   AAAA = year + J + TT + OOOO = segment + origin (positions 9-20)
          segment-origin (subs processo 9 20)
          ;;   DD = check digits (positions 7-8)
          check-digits (subs processo 7 9)

          rearranged (str sequential segment-origin check-digits)
          mod-result (helpers/mod-large-number rearranged 97)]
      (= mod-result 1))))
