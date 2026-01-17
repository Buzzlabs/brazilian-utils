(ns brazilian-utils.inscricao-estadual.core
  "Core utilities for Brazilian state registration (Inscrição Estadual)."
  (:require [brazilian-utils.helpers :as helpers]
            [brazilian-utils.inscricao-estadual.validation :as validation]))

;; ============================================================================
;; Public API
;; ============================================================================

(defn is-valid?
  "Validates a Brazilian state registration number (Inscrição Estadual).

  This function checks if the provided IE (Inscrição Estadual) is valid for
  a specific state. Each Brazilian state has different validation rules and
  expected formats for the IE number.

  Arguments:
    uf - State keyword (e.g., :SP, :RJ, :MG, :BA)
    ie - String with the IE (formatted or unformatted)

  Returns:
    true if the IE is valid for the given state; false otherwise

  Examples:
    (is-valid? :SP \"110042490114\") ;; => true
    (is-valid? :SP \"110042490115\") ;; => false
    (is-valid? :RJ \"12345\") ;; => false
    (is-valid? :XX \"123456\") ;; => false (invalid state)"
  [uf ie]
  (validation/is-valid? uf ie))

(defn remove-symbols
  "Removes all non-numeric characters from an IE (Inscrição Estadual) string.

  This function normalizes IE input by stripping formatting characters,
  returning only the digits.

  Arguments:
    ie - String with the IE (formatted or unformatted); nil allowed

  Returns:
    Digits-only string; nil yields an empty string

  Examples:
    (remove-symbols \"11.004.249.0114\") ;; => \"110042490114\"
    (remove-symbols \"11 004 249 0114\") ;; => \"110042490114\"
    (remove-symbols \"110042490114\") ;; => \"110042490114\"
    (remove-symbols nil) ;; => \"\"
    (remove-symbols \"\") ;; => \"\""
  [ie]
  (if (string? ie)
    (helpers/only-numbers ie)
    ""))