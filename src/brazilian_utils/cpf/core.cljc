(ns brazilian-utils.cpf.core
  (:require [brazilian-utils.cpf.internal :as i]
            [brazilian-utils.cpf.format :as fmt]
            [brazilian-utils.cpf.validation :as validation]
            [brazilian-utils.states :as states]
            [brazilian-utils.helpers :as helpers]))


(defn clean
  "Removes all non-numeric characters from a CPF.

  This function normalizes CPF input by removing formatting characters like dots
  and hyphens, returning only the digits.

  Args:
    cpf - The CPF string to clean (may include formatting)

  Returns:
    A string containing only digits (0-9)

  Examples:
    (clean \"123.456.789-09\") ;; => \"12345678909\"
    (clean \"12345678909\")     ;; => \"12345678909\"
    (clean \"\")                ;; => \"\""
  [cpf]
  (helpers/only-numbers cpf))

(defn format-cpf
  "Formats a CPF string with standard Brazilian punctuation (XXX.XXX.XXX-XX).
   Supports partial inputs and optional zero-padding."
  ([cpf] (fmt/format-cpf cpf))
  ([cpf opts] (fmt/format-cpf cpf opts)))

(defn generate
  "Generates a valid random numeric CPF.

  Creates a random 9-digit base, calculates the proper check digits using
  the Brazilian CPF algorithm, and returns a complete 11-digit valid CPF.
  The generated CPF is guaranteed to pass validation and will not be one
  of the reserved (all-repeated-digits) numbers.

  Args:
    state - Optional. The Brazilian state code keyword (e.g., :SP, :RJ) to use as the 9th digit.
            If not provided or invalid, a random digit is used.

  Returns:
    A valid 11-digit numeric CPF string (unformatted)

  Examples:
    (generate) ;; => \"12345678909\"
    (generate :SP) ;; => \"12345678901\" (with SP state code)"
  ([]
   (generate nil))
  ([state]
   (let [state-code (or (some-> state
                                states/uf->code
                                #?(:clj Integer/parseInt
                                   :cljs js/parseInt))
                        (rand-int 10))
         base (->> (repeatedly #(rand-int 10))
                   (take 8)
                   (apply str)
                   (iterate (fn [_] (apply str (repeatedly 8 #(rand-int 10)))))
                   (drop-while #(helpers/repeated-digits? (str % state-code)))
                   first
                   (#(str % state-code)))
         dv1 (i/calc-check-digit* base i/first-check-digit-weight)
         dv2 (i/calc-check-digit* (str base dv1) i/second-check-digit-weight)]
     (str base dv1 dv2))))

(defn is-valid?
  "Validates a CPF string.

  Checks if the provided CPF is valid by verifying:
  - It is a string
  - It has exactly 11 digits (after cleaning)
  - It is not a reserved number (all repeated digits)
  - The check digits are correct according to the Brazilian algorithm

  Accepts both formatted (XXX.XXX.XXX-XX) and unformatted (XXXXXXXXXXX) CPFs.

  Args:
    cpf - The CPF string to validate (formatted or unformatted)

  Returns:
    true if valid, false otherwise

  Examples:
    (is-valid? \"123.456.789-09\") ;; => true or false
    (is-valid? \"12345678909\")     ;; => true or false
    (is-valid? \"00000000000\")     ;; => false (reserved)
    (is-valid? \"12345678900\")     ;; => false (invalid check digit)
    (is-valid? nil)                 ;; => false"
  [cpf]
(if-not (string? cpf)
   false
   (and (validation/is-valid-format? cpf)
        (let [cleaned (clean cpf)]
          (boolean
           (and (helpers/only-numbers cleaned)
                (not (helpers/repeated-digits? cleaned))
                (i/valid-checksum* cleaned)))))))