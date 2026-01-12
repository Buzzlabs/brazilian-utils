(ns brazilian-utils.states.validation
  "Validation schemas for Brazilian states (UF).
   
   Uses malli to define and validate state-related data structures."
  (:require [malli.core :as m]))

;; ============================================================================
;; State (UF) Schemas
;; ============================================================================

(def State
  "Schema for Brazilian state code (UF).
   
   Accepts any of the 27 Brazilian state codes.
   
   Example:
   (m/validate State :SP) ;; true
   (m/validate State :MG) ;; true
   (m/validate State :XX) ;; false"
  [:enum
   :AC :AL :AP :AM :BA :CE :DF :ES :GO :MA :MG :MT :MS
   :PA :PB :PE :PI :PR :RJ :RN :RO :RS :RR :SC :SE :SP :TO])

(def StateData
  "Schema for a state's structured data.
   
   Contains the following fields:
   - code: Unique numeric code for the state
   - name: Full state name
   - area-codes: Vector of telephone area codes (DDDs)
   - ie-length: Length of the State Registration (IE) in digits
   
   Example:
   {:code \"8\" 
    :name \"São Paulo\" 
    :area-codes [11 12 13 14 15 16 17 18 19] 
    :ie-length 12}"
  [:map
  [:code {:doc "Unique numeric state code"} string?]
  [:name {:doc "Full state name"} string?]
  [:area-codes {:doc "Telephone area codes (DDDs)"} [:vector int?]]
   [:ie-length
   {:doc "State Registration (IE) length in digits"}
    [:or int? [:vector int?]]]])

(def StatesDataByUf
  "Schema for the state data map (as in resources/states.edn).
   
   Maps each UF to its structured state data.
   
   Example:
   {:SP {:code \"8\" :name \"São Paulo\" :area-codes [...] :ie-length 12}
    :RJ {:code \"7\" :name \"Rio de Janeiro\" :area-codes [...] :ie-length 8}}"
  [:map-of State StateData])

;; ============================================================================
;; Validation Functions
;; ============================================================================

(defn valid-uf?
  "Returns true if uf is a valid Brazilian state keyword.
   
   Arguments:
   - uf: Keyword representing the state abbreviation
   
   Returns true if valid, false otherwise.
   
   Example:
   (valid-uf? :SP) ;; true
   (valid-uf? :XX) ;; false"
  [uf]
  (m/validate State uf))

(defn validate-states-map
  "Validates that a map conforms to the full states data structure.
   
   Returns true if valid, false otherwise.
   
   Example:
   (validate-states-map {:SP {:code \"8\" :name \"São Paulo\" ...}
                         :RJ {:code \"7\" :name \"Rio de Janeiro\" ...}})
   ;; true"
  [value]
  (m/validate StatesDataByUf value))
