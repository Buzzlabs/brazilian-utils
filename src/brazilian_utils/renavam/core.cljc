(ns brazilian-utils.renavam.core
  "Core utilities for RENAVAM validation."
  (:require [brazilian-utils.helpers :as helpers]
            [brazilian-utils.renavam.validation :as validation]))

(defn is-valid?
  "Validates a RENAVAM (Registro Nacional de Veículo Automotor) number.

  This function checks if the input is a valid Brazilian RENAVAM by verifying:
  - It is a string
  - It contains exactly 9 or 11 digits (after cleaning formatting)
  - The check digit is correct according to the RENAVAM algorithm

  Accepts both formatted (XX.XXX.XXX-XX) and unformatted (XXXXXXXXXXX) RENAVAMs.
  Also accepts the 9-digit format (XXXXXXXXX) from older registrations.

  Arguments:
    renavam - RENAVAM string to validate (formatted or unformatted)

  Returns:
    true if valid; false otherwise

  Examples:
    (is-valid? \"12345678901\") ;; true/false depending on check digit
    (is-valid? \"123456789\")   ;; true/false (9-digit format)
    (is-valid? \"12345678\")    ;; false (8 digits)
    (is-valid? nil)             ;; false"
  [renavam]
  (validation/is-valid? renavam))



(defn remove-symbols
  "Removes all non-numeric characters from a RENAVAM.

  This function normalizes RENAVAM input by stripping formatting characters,
  returning only the digits.

  Arguments:
    renavam - RENAVAM string (formatted or unformatted); nil allowed

  Returns:
    Digits-only string; nil yields an empty string

  Examples:
    (remove-symbols \"12345678901\") ;; => \"12345678901\"
    (remove-symbols \"12.345.678-90\") ;; => \"1234567890\"
    (remove-symbols nil)               ;; => \"\"
    (remove-symbols \"\")            ;; => \"\""
  [renavam]
  (if (string? renavam)
    (helpers/only-numbers renavam)
    ""))
