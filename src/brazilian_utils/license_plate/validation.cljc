(ns brazilian-utils.license-plate.validation
  "Validation schemas for Brazilian license plates.
   
   Supports both traditional plates (3 letters + 4 digits) and 
   Mercosul plates (3 letters + 1 digit + 1 letter + 2 digits).
   
   Uses malli to define and validate license plate data structures."
  (:require [malli.core :as m]))

(def MercosulLicensePlate
  "Schema for Mercosul license plate format (newer plates).
   Format: 3 letters + 1 digit + 1 letter + 2 digits (e.g., ABC1D23).
   
   Example:
   (m/validate MercosulLicensePlate \"ABC1D23\")  ;; true
   (m/validate MercosulLicensePlate \"ABC-1D23\") ;; false (no formatting)"
  [:re #"(?i)^[a-z]{3}[0-9]{1}[a-z]{1}[0-9]{2}$"])

(def BrazilianLicensePlate
  "Schema for traditional Brazilian license plate format.
   Format: 3 letters + 4 digits, with optional hyphen (e.g., ABC-1234 or ABC1234).
   
   Example:
   (m/validate BrazilianLicensePlate \"ABC1234\")  ;; true
   (m/validate BrazilianLicensePlate \"ABC-1234\") ;; true
   (m/validate BrazilianLicensePlate \"AB1234\")   ;; false (only 2 letters)"
  [:re #"(?i)^[a-z]{3}-?[0-9]{4}$"])

(def AnyLicensePlate
  "Schema for any valid Brazilian license plate (Mercosul or traditional).
   
   Example:
   (m/validate AnyLicensePlate \"ABC1D23\")  ;; true (Mercosul)
   (m/validate AnyLicensePlate \"ABC-1234\") ;; true (traditional)"
  [:or MercosulLicensePlate BrazilianLicensePlate])

(defn validate-license-plate
  "Validates whether a string is a valid Brazilian license plate.
   
   Accepts both Mercosul (3 letters + 1 digit + 1 letter + 2 digits)
   and traditional (3 letters + 4 digits) formats.
   
   Returns true if valid, false otherwise.
   
   Example:
   (validate-license-plate \"ABC1D23\")  ;; true (Mercosul)
   (validate-license-plate \"ABC-1234\") ;; true (traditional)
   (validate-license-plate \"ABC1234\")  ;; false (ambiguous - could be traditional)
   (validate-license-plate \"invalid\")  ;; false"
  [value]
  (and (string? value)
       (not (empty? value))
       (m/validate AnyLicensePlate value)))
