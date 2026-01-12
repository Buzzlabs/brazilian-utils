(ns brazilian-utils.license-plate.core
  "Brazilian license plate validation and formatting utilities.
   
   Provides functions to validate and format both traditional (ABCD1234)
   and Mercosul (ABC1D23) Brazilian license plates.
   
   Functions:
     - is-valid?: Validates any license plate format
     - is-mercosul?: Validates Mercosul format
     - is-brazilian?: Validates traditional format
     - clean: Removes formatting characters
     - format: Formats plate with hyphen (for traditional plates only)"
  (:require [brazilian-utils.license-plate.validation :as validation]))

;; ============================================================================
;; Public API
;; ============================================================================

(defn is-valid?
  "Validates if a string is a valid Brazilian license plate.
   
   Accepts both:
   - Mercosul format: 3 letters + 1 digit + 1 letter + 2 digits (e.g., ABC1D23)
   - Traditional format: 3 letters + 4 digits (e.g., ABC-1234 or ABC1234)
   
   Args:
     value - String to validate
   
   Returns:
     true if valid license plate, false otherwise
     
   Examples:
     (is-valid? \"ABC1D23\")  ;; true (Mercosul)
     (is-valid? \"ABC-1234\") ;; true (traditional)
     (is-valid? \"ABC1234\")  ;; false (ambiguous format)
     (is-valid? nil)        ;; false
     (is-valid? \"invalid\") ;; false"
  [value]
  (validation/validate-license-plate value))
