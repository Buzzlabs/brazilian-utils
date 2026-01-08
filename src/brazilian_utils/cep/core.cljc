(ns brazilian-utils.cep.core
  (:require [brazilian-utils.cep.schemas :as schemas]
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
   
   Checks:
   - Type (must be string)
   - Format (8 numeric digits)
   
   Arguments:
   - cep: String containing the CEP for validation
   
   Returns true if valid, false otherwise.
   
   Example:
   (is-valid? \"01310-100\") ;; true
   (is-valid? \"01310100\")  ;; true
   (is-valid? \"0131010\")   ;; false (7 digits)
   (is-valid? nil)         ;; false"
  [cep]
  (if (or (not cep) (not (string? cep)))
    false
    (let [digits (helpers/only-numbers cep)]
      (try
        (schemas/validate-cep digits)
        (catch #?(:clj Exception :cljs :default) _
          false)))))

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