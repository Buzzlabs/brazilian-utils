(ns brazilian-utils.cep.core
  (:require [brazilian-utils.cep.validation :as validation]
            [brazilian-utils.cep.internal :as i]
            [brazilian-utils.helpers :as helpers]))

;; ============================================================================
;; Constants
;; ============================================================================

(def ^:private cep-length
  "Standard length of a Brazilian CEP (postal code)."
  8)

(def ^:private formatting-length
  "Length when we should be inserted hyphen when formatting."
  6)

(def ^:private hyphen-index
  "Index where hyphen should be inserted in formatting."
  5)

;; ============================================================================
;; Public API
;; ============================================================================

(defn is-valid?
  "Validates whether a CEP (postal code) is well-formed.
   
  Checks:
  - Input is a string
  - Contains only digits and an optional hyphen in the correct position
  - Has exactly 8 digits
  - All characters are numeric (0-9)

  Accepts both formatted (XXXXX-XXX) and unformatted (XXXXXXXX) CEPs.

  Args:
    cep - CEP string to validate (formatted or unformatted)

  Returns:
    true if valid; false otherwise
   
  Examples:
    (is-valid? \"01310-100\") ;; true
    (is-valid? \"01310100\")  ;; true
    (is-valid? \"0131010\")   ;; false (7 digits)
    (is-valid? nil)             ;; false"
  [cep]
  (if-not (string? cep)
    false
    (validation/validate-cep cep)))

(defn remove-symbols
  "Removes all non-numeric characters from a CEP.

  Normalizes CEP input by stripping hyphens and other symbols, returning only digits.

  Args:
    cep - CEP string to normalize (formatted or unformatted); nil allowed

  Returns:
    String with digits only (0-9); nil yields an empty string

  Examples:
    (remove-symbols \"01310-100\") ;; => \"01310100\"
    (remove-symbols \"01310100\")  ;; => \"01310100\"
    (remove-symbols nil)             ;; => \"\"
    (remove-symbols \"\")          ;; => \"\""
  [cep]
  (helpers/only-numbers cep))

(defn format-cep
  "Formats a CEP with standard Brazilian mask (XXXXX-XXX).
  
  Arguments:
    cep - String or number to format (formatted or unformatted)

  Returns:
    A CEP string formatted with the mask (XXXXX-XXX)
    
  Examples:
    (format-cep \"01310100\") ;; => \"01310-100\"
    (format-cep \"01310-100\") ;; => \"01310-100\"
    (format-cep \"01310\") ;; => \"01310\""
  [cep]
  (let [digits (helpers/only-numbers (str cep))
        max-length (min cep-length (count digits))
        cleaned (subs digits 0 max-length)]
    (if (>= (count cleaned) formatting-length)
      (helpers/split-and-rejoin cleaned [hyphen-index] "-")
      cleaned)))

;; ============================================================================
;; ViaCEP API Integration
;; ============================================================================

(defn get-address-from-cep
  "Retrieves address information for a given CEP using ViaCEP API.
  
  Args:
    cep (string): The CEP code (8 digits, with or without hyphen)
    
  Returns:
    A map containing address information on success, or an error map on failure.
    Success: {:logradouro \"street name\", :bairro \"neighborhood\", :localidade \"city\", :uf \"state\"}
    Error: {:error error-message}
    
  Examples:
    (get-address-from-cep \"01310-100\")
    (get-address-from-cep \"01310100\")"
  [cep]
  (helpers/safe-call
   #(let [cep-clean (helpers/only-numbers cep)
          url (i/build-viacep-url cep-clean)
          response (helpers/http-get url)]
      (if (contains? response :error)
        response
        (:body response)))
   {:error "Request failed"}))

(defn get-cep-information-from-address
  "Searches for CEP code by address information using ViaCEP API.
  Returns the first (most relevant) result.
  
  This function queries the ViaCEP API to find a CEP based on street name, city, and state.
  The API may return multiple results, but this function returns only the first (most relevant) match.
  
  Arguments:
    logradouro (string): Street name
    localidade (string): City name
    uf (string): State abbreviation (2 letters, e.g., \"SP\", \"RJ\")
    
  Returns:
    A map with address information on success, or an error map on failure.
    Success: {:logradouro \"street\", :bairro \"neighborhood\", :localidade \"city\", :uf \"state\", :cep \"12345-678\", :ddd \"11\", :ibge \"3550308\"}
    Error: {:error error-message}
    
  Examples:
    (get-cep-information-from-address \"Avenida Paulista\" \"São Paulo\" \"SP\")
    (get-cep-information-from-address \"Rua Augusta\" \"São Paulo\" \"SP\")"
  [logradouro localidade uf]
  (helpers/safe-call
   #(let [url (i/build-viacep-address-search-url uf localidade logradouro)
          response (helpers/http-get url)]
      (if (contains? response :error)
        response
        (let [results (:body response)]
          (if (and (vector? results) (seq results))
            (first results)
            {:error "No results found"}))))
   {:error "Request failed"}))
