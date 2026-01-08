(ns brazilian-utils.cep.schemas
  "Validation schemas for CEP (Brazilian postal code).
   
   Uses malli to define and validate CEP-related data structures."
  (:require [malli.core :as m]))

;; ============================================================================
;; CEP Schemas
;; ============================================================================

(def CEPNumber
  "Schema for unformatted CEP (8 numeric digits).
   
   Example:
   (m/validate CEPNumber \"01310100\") ;; true
   (m/validate CEPNumber \"0131010\")  ;; false (fewer than 8)
   (m/validate CEPNumber \"013101000\") ;; false (more than 8)"
  [:re #"^\d{8}$"])

(def CEPFormatted
  "Schema for formatted CEP (xxxxx-xxx).
   
   Example:
   (m/validate CEPFormatted \"01310-100\") ;; true
   (m/validate CEPFormatted \"01310100\")  ;; false"
  [:re #"^\d{5}-\d{3}$"])

(defn validate-cep
  "Validates whether a string is a valid CEP (formatted or not).
   
   Returns true if valid, false otherwise.
   
   Example:
   (validate-cep \"01310100\")  ;; true
   (validate-cep \"01310-100\") ;; true
   (validate-cep \"0131010\")   ;; false"
  [value]
  (or (m/validate CEPNumber value)
      (m/validate CEPFormatted value)))
