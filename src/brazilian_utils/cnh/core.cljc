(ns brazilian-utils.cnh.core
  "Core utilities for CNH (Carteira Nacional de Habilitação)."
  (:require [brazilian-utils.helpers :as helpers]
            [brazilian-utils.cnh.validation :as validation]))

(defn is-valid?
  "Validates a CNH number.

  Input: string with 11 digits (formatted or not); non-strings return false.
  Output: true when structure and check digits are valid; otherwise false."
  [cnh]
  (validation/is-valid? cnh))



(defn remove-symbols
  "Removes non-digit characters from a CNH string.

  Input: string or nil (formatted or not).
  Output: digits-only string; nil yields an empty string."
  [cnh]
  (if (string? cnh)
    (helpers/only-numbers cnh)
    ""))
