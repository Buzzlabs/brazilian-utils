(ns brazilian-utils.cep.core
  (:require [brazilian-utils.cep.validation :as validation]
            [brazilian-utils.helpers :as helpers]))

;; ============================================================================
;; Constants
;; ============================================================================

(def ^:private length
  "Expected length of CEP (without formatting)."
  8)

(def ^:private hyphen-index
  "Index where hyphen should be inserted in formatting."
  5)

;; ============================================================================
;; Public API
;; ============================================================================

(defn is-valid?
  "Validates whether a CEP (postal code) is well-formed.
   
  Checks:
  - Input is a string
  - Contains only digits and an optional hyphen in the correct position
  - Has exactly 8 digits
  - All characters are numeric (0-9)

  Accepts both formatted (XXXXX-XXX) and unformatted (XXXXXXXX) CEPs.

  Args:
    cep - CEP string to validate (formatted or unformatted)

  Returns:
    true if valid; false otherwise
   
  Examples:
    (is-valid? \"01310-100\") ;; true
    (is-valid? \"01310100\")  ;; true
    (is-valid? \"0131010\")   ;; false (7 digits)
    (is-valid? nil)             ;; false"
  [cep]
  (if-not (string? cep)
    false
    (validation/validate-cep cep)))

(defn remove-symbols
  "Removes all non-numeric characters from a CEP.

  Normalizes CEP input by stripping hyphens and other symbols, returning only digits.

  Args:
    cep - CEP string to normalize (formatted or unformatted); nil allowed

  Returns:
    String with digits only (0-9); nil yields an empty string

  Examples:
    (remove-symbols \"01310-100\") ;; => \"01310100\"
    (remove-symbols \"01310100\")  ;; => \"01310100\"
    (remove-symbols nil)             ;; => \"\"
    (remove-symbols \"\")          ;; => \"\""
  [cep]
  (helpers/only-numbers cep))

(defn format-cep
  "Formats a CEP by adding hyphen at the correct position (xxxxx-xxx).
   
   Arguments:
   - cep: String containing the CEP to be formatted
   
   Returns a formatted string with hyphen.
   Removes non-numeric characters and limits to 8 digits.
   
  Example:
  (format-cep \"01310100\")  ;; \"01310-100\"
  (format-cep \"01310-100\") ;; \"01310-100\"
  (format-cep \"013101\")    ;; \"01310-1\" (partial)"
  [cep]
  (let [digits (helpers/only-numbers cep)
        normalized-cep (subs digits 0 (min length (count digits)))
        [prefix suffix] (split-at hyphen-index normalized-cep)]
    (str (apply str prefix)
         (when (seq suffix) "-")
         (apply str suffix))))