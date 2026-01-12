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
  "Validates if a CEP (Postal Code) is valid.
   
  Checks if the provided CEP is valid by verifying:
  - It is a string
  - It contains only digits and optionally a hyphen in the correct position
  - It has exactly 8 digits
  - All digits are numeric (0-9)

  Accepts both formatted (XXXXX-XXX) and unformatted (XXXXXXXX) CEPs.

  Args:
    cep - The CEP string to validate

  Returns:
    true if valid, false otherwise
   
   Example:
   (is-valid? \"01310-100\") ;; true
   (is-valid? \"01310100\")  ;; true
   (is-valid? \"0131010\")   ;; false (7 digits)
   (is-valid? nil)         ;; false"
  [cep]
  (if-not (string? cep)
    false
    (validation/validate-cep cep)))

(defn clean
  "Removes all non-numeric characters from a CEP.

  This function normalizes CEP input by removing formatting characters like hyphens,
  returning only the digits.

  Args:
    cep - The CEP string to clean (may include formatting)

  Returns:
    A string containing only digits (0-9)

  Examples:
    (clean \"01310-100\") ;; => \"01310100\"
    (clean \"01310100\")  ;; => \"01310100\"
    (clean \"\")          ;; => \"\""
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