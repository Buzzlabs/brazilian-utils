(ns brazilian-utils.cnh.core
  "Core utilities for CNH (Carteira Nacional de Habilitação)."
  (:require [brazilian-utils.helpers :as helpers]
            [brazilian-utils.cnh.validation :as validation]))

(defn is-valid?
  "Validates a CNH (Carteira Nacional de Habilitação/National Driver License) number.

  This function checks if the input is a valid Brazilian CNH by verifying:
  - It is a string
  - It contains exactly 11 digits (after cleaning formatting)
  - The check digits are correct according to the CNH algorithm

  Accepts both formatted (XXXXX.XXXX.XXXX) and unformatted (XXXXXXXXXXX) CNHs.

  Arguments:
    cnh - CNH string to validate (formatted or unformatted)

  Returns:
    true if valid; false otherwise

  Examples:
    (is-valid? \"00000000191\") ;; true/false depending on check digits
    (is-valid? \"0000000019\") ;; false (10 digits)
    (is-valid? nil)            ;; false"
  [cnh]
  (validation/is-valid? cnh))



(defn remove-symbols
  "Removes all non-numeric characters from a CNH.

  This function normalizes CNH input by stripping formatting characters,
  returning only the digits.

  Arguments:
    cnh - CNH string (formatted or unformatted); nil allowed

  Returns:
    Digits-only string; nil yields an empty string

  Examples:
    (remove-symbols \"00000000191\") ;; => \"00000000191\"
    (remove-symbols \"0000000019\")  ;; => \"0000000019\"
    (remove-symbols nil)               ;; => \"\"
    (remove-symbols \"\")            ;; => \"\""
  [cnh]
  (if (string? cnh)
    (helpers/only-numbers cnh)
    ""))
