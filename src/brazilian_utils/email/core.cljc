(ns brazilian-utils.email.core
  (:require [brazilian-utils.email.validation :as validation]))

(defn is-valid?
  "Validates whether a given value is a well-formed email address.

  Args:
    email - The value to validate as an email address.

  Returns:
    true if the provided value is a string that satisfies all email validation checks; 
    otherwise false.

  Examples:
    (is-valid? \"user@example.com\") ;; => true
    (is-valid? \"\")                 ;; => false
    (is-valid? nil)                  ;; => false
    (is-valid? \"verylong...@ex.com\") ;; => false if exceeds configured limits"
  [email]
  (validation/validate-email email))

(defn validation-errors
  "Validates an email and returns detailed error information if invalid.

  Returns an empty vector for valid emails, or a vector of error message strings for invalid emails.

  Args:
    email - The value to validate as an email address.

  Returns:
    Empty vector if the email is valid, or a vector of error message strings if invalid.

  Examples:
    (validation-errors \"user@example.com\") ;; => []
    (validation-errors \"\") ;; => [\"Email cannot be blank\"]
    (validation-errors nil) ;; => [\"should be a string\"]"
  [email]
  (if (validation/validate-email email)
    []
    (validation/explain-email email)))
