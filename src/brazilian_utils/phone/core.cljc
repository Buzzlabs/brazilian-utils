(ns brazilian-utils.phone.core
  (:require [brazilian-utils.phone.validation :as validation]))

(defn is-valid?
  "Validates whether a given value is a well-formed Brazilian phone number.

  Accepts both mobile (11 digits) and landline (10 digits) phone numbers.
  The validation checks:
  1. The value is a non-empty string
  2. Contains only digits and formatting characters
  3. Has correct length (10 or 11 digits after removing formatting)
  4. Has valid Brazilian area code (DDD)
  5. Has valid first digit after area code based on phone type

  Args:
    phone - The phone number string to validate (may include formatting)

  Returns:
    true if the phone number is valid; false otherwise

  Examples:
    (is-valid? \"(11) 9 0000-0000\") ;; => true (mobile)
    (is-valid? \"(11) 3000-0000\")   ;; => true (landline)
    (is-valid? \"11900000000\")      ;; => true (mobile without mask)
    (is-valid? \"1130000000\")       ;; => true (landline without mask)
    (is-valid? \"\")                 ;; => false
    (is-valid? \"123\")              ;; => false"
  [phone]
  (validation/is-valid-phone? phone))

(defn is-valid-mobile?
  "Validates whether a given value is a well-formed Brazilian mobile phone number.

  Mobile phones must have exactly 11 digits and the first digit after the area code
  must be 6, 7, 8, or 9.

  Args:
    phone - The phone number string to validate (may include formatting)

  Returns:
    true if the phone number is a valid mobile phone; false otherwise

  Examples:
    (is-valid-mobile? \"(11) 9 0000-0000\") ;; => true
    (is-valid-mobile? \"11900000000\")      ;; => true
    (is-valid-mobile? \"(11) 3000-0000\")   ;; => false (landline)"
  [phone]
  (validation/is-valid-mobile-phone? phone))

(defn is-valid-landline?
  "Validates whether a given value is a well-formed Brazilian landline phone number.

  Landline phones must have exactly 10 digits and the first digit after the area code
  must be 2, 3, 4, or 5.

  Args:
    phone - The phone number string to validate (may include formatting)

  Returns:
    true if the phone number is a valid landline phone; false otherwise

  Examples:
    (is-valid-landline? \"(11) 3000-0000\") ;; => true
    (is-valid-landline? \"1130000000\")     ;; => true
    (is-valid-landline? \"(11) 9 0000-0000\") ;; => false (mobile)"
  [phone]
  (validation/is-valid-landline-phone? phone))

(defn validation-errors
  "Validates a phone number and returns detailed error information if invalid.

  Returns an empty vector for valid phones, or a vector of error message strings for invalid phones.

  Args:
    phone - The phone number string to validate

  Returns:
    Empty vector if the phone is valid, or a vector of error message strings if invalid.

  Examples:
    (validation-errors \"(11) 9 0000-0000\") ;; => []
    (validation-errors \"\") ;; => [\"Phone number cannot be blank\"]
    (validation-errors \"123\") ;; => [\"Phone number must have between 10 and 11 digits\"]"
  [phone]
  (validation/explain-phone phone))

(defn mobile-validation-errors
  "Validates a mobile phone number and returns detailed error information if invalid.

  Returns an empty vector for valid mobile phones, or a vector of error message strings for invalid phones.

  Args:
    phone - The phone number string to validate

  Returns:
    Empty vector if the mobile phone is valid, or a vector of error message strings if invalid."
  [phone]
  (validation/explain-mobile-phone phone))

(defn landline-validation-errors
  "Validates a landline phone number and returns detailed error information if invalid.

  Returns an empty vector for valid landline phones, or a vector of error message strings for invalid phones.

  Args:
    phone - The phone number string to validate

  Returns:
    Empty vector if the landline phone is valid, or a vector of error message strings if invalid."
  [phone]
  (validation/explain-landline-phone phone))