(ns brazilian-utils.inscricao-estadual.core
  "Core utilities for Brazilian state registration (Inscrição Estadual)."
  (:require [brazilian-utils.helpers :as helpers]
            [brazilian-utils.inscricao-estadual.validation :as validation]))

;; ============================================================================
;; Public API
;; ============================================================================

(defn is-valid?
  "Validates a Brazilian state registration number (Inscrição Estadual).

  Args:
    uf - State keyword (e.g., :SP, :RJ, :MG, :BA)
    ie - String with the IE (formatted or unformatted)

  Returns:
    true if the IE is valid for the given state; false otherwise

  Examples:
    (is-valid? :SP \"110042490114\") ;; => true
    (is-valid? :SP \"110042490115\") ;; => false
    (is-valid? :RJ \"12345\") ;; => false"
  [uf ie]
  (validation/is-valid? uf ie))

(defn remove-symbols
  "Removes non-digit characters from an IE string.

  Args:
    ie - String or nil (formatted or not)

  Returns:
    digits-only string; nil yields an empty string

  Examples:
    (remove-symbols \"11.004.249.0114\") ;; => \"110042490114\"
    (remove-symbols \"11 004 249 0114\") ;; => \"110042490114\"
    (remove-symbols nil) ;; => \"\""
  [ie]
  (if (string? ie)
    (helpers/only-numbers ie)
    ""))