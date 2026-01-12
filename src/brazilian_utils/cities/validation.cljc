(ns brazilian-utils.cities.validation
  "Validation validation for Brazilian cities.
   
   Uses malli to define and validate city-related data structures."
  (:require [malli.core :as m]
            [brazilian-utils.states.validation :as validation]))

;; ============================================================================
;; City (Cidade) validation  
;; ============================================================================

(def City
  "Schema for city name. Any non-empty string."
  [:and string? [:fn {:error/message "city cannot be empty"}
                 (fn [s] (not (empty? s)))]])

(def CitiesByUf
  "Schema for cities-by-state map containing only city names (see resources/cities.edn).
   
   Structure:
   {:UF [\"City Name 1\" \"City Name 2\" ...]}
   
   Example:
   {:SP [\"São Paulo\" \"Campinas\" ...]
    :RJ [\"Rio de Janeiro\" \"Niterói\" ...]}"
  [:map-of validation/State [:vector City]])

;; ============================================================================
;; Validation Functions
;; ============================================================================

(defn validate-cities-map
  "Validates that a map contains the full cities-by-state structure.
   
   Returns true if valid, false otherwise.
   
   Example:
   (validate-cities-map {:SP [\"São Paulo\" \"Campinas\" ...]
                         :RJ [\"Rio de Janeiro\" \"Niterói\" ...]})
   ;; true"
  [value]
  (m/validate CitiesByUf value))
