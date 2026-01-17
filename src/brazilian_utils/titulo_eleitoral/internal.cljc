(ns brazilian-utils.titulo-eleitoral.internal
  "Internal utilities for Título Eleitoral validation."
  (:require [brazilian-utils.helpers :as helpers]))

;; Official TSE state codes (different from IBGE codes)
;; These are the codes used in Voter ID numbers
(def valid-uf-codes
  #{1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28
    31 32 33 34 35 36 37 38 39 40 41 42 43 44 45 46 47 48 49 50 51 52 53})

(defn calculate-first-digit
  "Calculates the first check digit (DV1) of a Voter ID.

   Uses the first 8 digits with weights [2 3 4 5 6 7 8 9].

   Args:
     base-digits - String with first 8 digits
     uf-code - String with 2-digit UF code (validated for length)

   Returns:
     Integer representing the first check digit (0-9) or nil when inputs
     do not match expected lengths."
  [base-digits uf-code]
  (when (and (= 8 (count base-digits)) (= 2 (count uf-code)))
    (helpers/check-digit base-digits [2 3 4 5 6 7 8 9])))

(defn calculate-second-digit
  "Calculates the second check digit (DV2) of a Voter ID.

   Uses the 2-digit UF code and DV1 with weights [7 8 9].

   Args:
     uf-code - String with 2-digit UF code
     first-digit - Integer representing DV1

   Returns:
     Integer representing the second check digit (0-9) or nil when inputs
     do not match expected lengths."
  [uf-code first-digit]
  (let [digits (str uf-code first-digit)]
    (when (= 3 (count digits))
      (helpers/check-digit digits [7 8 9]))))

(defn valid-check-digits?
  "Validates both check digits of a Voter ID.

   Args:
     voter-id - String with 12 digits (cleaned)

   Returns:
     true if both check digits are valid, false otherwise."
  [voter-id]
  (when (and (string? voter-id) (= 12 (count voter-id)))
    (let [base (subs voter-id 0 8)
          uf-code (subs voter-id 10 12)
          expected-dv1 (calculate-first-digit base uf-code)
          expected-dv2 (and expected-dv1 (calculate-second-digit uf-code expected-dv1))
          actual-dv1 (helpers/char->digit (get voter-id 8))
          actual-dv2 (helpers/char->digit (get voter-id 9))]
      (and expected-dv1 expected-dv2
           (= expected-dv1 actual-dv1)
           (= expected-dv2 actual-dv2)))))

(defn valid-uf-code?
  "Checks if UF code is valid according to TSE rules.

   Args:
     uf-code - String with 2 digits or integer

   Returns:
     true if valid, false otherwise."
  [uf-code]
  (let [code (if (string? uf-code)
               (helpers/parse-int uf-code)
               uf-code)]
    (contains? valid-uf-codes code)))

(defn all-same-digit?
  "Checks if all digits are the same.

   Args:
     s - String to check

   Returns:
     true if all digits are the same, false otherwise."
  [s]
  (helpers/repeated-digits? s))
