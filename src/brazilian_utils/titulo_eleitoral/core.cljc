(ns brazilian-utils.titulo-eleitoral.core
  "Core utilities for Título Eleitoral.
   
   This module provides validation and generation utilities for
   Títulos Eleitorais. Validation checks mathematical correctness and
   structure, but does NOT verify if the ID exists in TSE's registry."
  (:require [brazilian-utils.titulo-eleitoral.internal :as internal]
            [brazilian-utils.titulo-eleitoral.validation :as validation]
            [brazilian-utils.helpers :as helpers]))

(defn- valid-uf-code-str
  "Normalizes a UF code (string or integer) to a 2-digit string when valid."
  [uf-code]
  (cond
    (string? uf-code)
    (let [clean (helpers/only-numbers uf-code)]
      (when (and (= 2 (count clean)) (internal/valid-uf-code? clean))
        clean))

    (number? uf-code)
    (let [code (int uf-code)]
      (when (internal/valid-uf-code? code)
        (if (< code 10) (str "0" code) (str code))))

    :else nil))

(defn- random-uf-code-str
  "Picks a random valid UF code from the official list and formats it."
  []
  (let [code (rand-nth (vec internal/valid-uf-codes))]
    (if (< code 10) (str "0" code) (str code))))

(defn is-valid?
  "Validates whether a given value is a well-formed Brazilian Voter ID.
   
   The validation checks:
   1. The value is a non-empty string
   2. Contains exactly 12 digits after removing formatting
   3. UF code (last 2 digits) is valid according to TSE rules
   4. Both check digits are mathematically correct
   5. Not all digits are the same
   
   **Important**: This validates structure and math, NOT existence in TSE registry.
   
   Args:
     voter-id - The voter ID string to validate (may include formatting)
   
   Returns:
     true if the voter ID is valid; false otherwise
   
   Examples:
     (is-valid? \"123456789035\") ;; depends on check digits
     (is-valid? \"1234 5678 9035\") ;; formatting ignored
     (is-valid? \"\") ;; => false
     (is-valid? nil) ;; => false"
  [voter-id]
  (validation/is-valid-voter-id? voter-id))

(defn validation-errors
  "Validates a voter ID and returns detailed error information if invalid.
   
   Returns an empty vector for valid IDs, or a vector of error message
   strings for invalid IDs.
   
   Args:
     voter-id - The voter ID string to validate
   
   Returns:
     Empty vector if valid, or vector of error message strings if invalid
   
   Examples:
     (validation-errors \"1234 5678 9035\") ;; [] or [\"Invalid check digits\"]
     (validation-errors \"\") ;; [\"Voter ID must have 12 digits after cleaning\"]"
  [voter-id]
  (validation/explain-voter-id voter-id))

(defn remove-symbols
  "Removes all non-numeric characters from a voter ID string.
   
   Args:
     voter-id - String with voter ID (may include formatting)
   
   Returns:
     String with only digits, or empty string if input is nil
   
   Examples:
     (remove-symbols \"1234 5678 9035\") ;; => \"123456789035\"
     (remove-symbols \"1234-5678-9035\") ;; => \"123456789035\"
     (remove-symbols nil) ;; => \"\""
  [voter-id]
  (if (string? voter-id)
    (helpers/only-numbers voter-id)
    ""))

(defn get-uf-code
  "Extracts the UF code from a voter ID.
   
   Args:
     voter-id - String with voter ID (may include formatting)
   
   Returns:
     String with 2-digit UF code, or nil if invalid
   
   Examples:
     (get-uf-code \"123456789035\") ;; => \"35\" (São Paulo)
     (get-uf-code \"1234 5678 9035\") ;; => \"35\"
     (get-uf-code \"invalid\") ;; => nil"
  [voter-id]
  (when (string? voter-id)
    (let [cleaned (helpers/only-numbers voter-id)]
      (when (= 12 (count cleaned))
        (subs cleaned 10 12)))))

(defn generate
  "Generates a valid Brazilian Voter ID (Título Eleitoral).

   Options map supports:
   - :uf-code => optional UF code (string or integer). When provided and
     invalid, returns nil. When omitted, a valid UF code is chosen at random.

   Returns a 12-digit string containing base, check digits, and UF code, or
   nil if generation fails after multiple attempts.

   Examples:
     (generate)                ;; => \"123456780104\" (example)
     (generate {:uf-code 35})  ;; => UF 35 (SP)
     (generate {:uf-code \"01\"})
  "
  ([] (generate {}))
  ([{:keys [uf-code]}]
   (when-let [uf (if (some? uf-code)
                   (valid-uf-code-str uf-code)
                   (random-uf-code-str))]
     (loop [attempt 0]
       (let [base (helpers/random-digits 8)
             dv1 (internal/calculate-first-digit base uf)
             dv2 (and dv1 (internal/calculate-second-digit uf dv1))
             candidate (when (and dv1 dv2) (str base dv1 dv2 uf))]
         (cond
           (and candidate (validation/is-valid-voter-id? candidate)) candidate
           (< attempt 10) (recur (inc attempt))
           :else nil))))))
