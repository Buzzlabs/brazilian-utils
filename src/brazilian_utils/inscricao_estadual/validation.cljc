(ns brazilian-utils.inscricao-estadual.validation
  "Validation schema for Brazilian State Registration (Inscrição Estadual)."
  (:require [malli.core :as m]
            [brazilian-utils.states.validation :as states-validation]
            [brazilian-utils.inscricao-estadual.internal :as internal]))

;; ============================================================================
;; Schemas
;; ============================================================================

(def UF
  "Valid Brazilian state code."
  states-validation/State)

(def InscricaoEstadualString
  "String containing IE (formatted or unformatted)."
  [:string {:min 1}])

;; ============================================================================
;; Core Validation
;; ============================================================================

(def InscricaoEstadualByState
  "Schema for state registration number with its state."
  [:and
   [:map
    [:uf UF]
    [:ie InscricaoEstadualString]]
   [:fn {:error/message "Invalid length for this state"}
    (fn [{:keys [uf ie]}]
      (internal/valid-length? uf ie))]
   [:fn {:error/message "Invalid check digits for this state"}
    (fn [{:keys [uf ie]}]
      (internal/validate-ie-by-state uf ie))]])

;; ============================================================================
;; Validation Functions
;; ============================================================================

(defn is-valid?
  "Validates a Brazilian state registration number (Inscrição Estadual).
   
   Returns true if:
   - uf is a valid state keyword
   - ie is a non-empty string
   - ie has correct length for the state
   - check digits are valid (where applicable)
   
   Returns false otherwise."
  [uf ie]
  (m/validate InscricaoEstadualByState {:uf uf :ie ie}))