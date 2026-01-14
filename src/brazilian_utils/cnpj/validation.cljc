(ns brazilian-utils.cnpj.validation
  (:require [malli.core :as m]))

(def CNPJNumeric
  "Schema for numeric CNPJ: exactly 14 digits"
  [:re #"^\d{14}$"])

(def CNPJFormatted
  "Schema for formatted numeric CNPJ: XX.XXX.XXX/XXXX-XX"
  [:re #"^\d{2}\.\d{3}\.\d{3}\/\d{4}-\d{2}$"])

(def CNPJAlfanumericStrict
  "Schema for unformatted alphanumeric CNPJ: 12 chars (A-Z or 0-9) + 2 numeric DVs"
  [:and
   [:re #"^[0-9A-Z]{12}\d{2}$"]  ; total length 14, last two must be digits
   [:re #"[A-Z]"]])               ; must contain at least one letter somewhere

(def CNPJAlfanumericFormatted
  "Schema for formatted alphanumeric CNPJ: XX.XXX.XXX/XXXX-XX (with letters)"
  [:and
   [:re #"^[0-9A-Z]{2}\.[0-9A-Z]{3}\.[0-9A-Z]{3}/[0-9A-Z]{4}-\d{2}$"]
   [:re #"[A-Z]"]])

(defn is-formatted-alfanumeric?
  "Checks if CNPJ is formatted (alphanumeric with mask).
  
  Args:
    cnpj - The CNPJ string to check
    
  Returns:
    true if formatted with alphanumeric mask, false otherwise"
  [cnpj]
  (and (string? cnpj) (m/validate CNPJAlfanumericFormatted cnpj)))

(defn is-formatted?
  "Checks if CNPJ is formatted (numeric with mask).
  
  Args:
    cnpj - The CNPJ string to check
    
  Returns:
    true if formatted with numeric mask, false otherwise"
  [cnpj]
  (and (string? cnpj) (m/validate CNPJFormatted cnpj)))

(defn is-numeric?
  "Returns true if the CNPJ (cleaned) is numeric with 14 digits.
  
  Args:
    cnpj - The CNPJ string to check
    
  Returns:
    true if numeric with 14 digits, false otherwise"
  [cnpj]
  (and (string? cnpj) (m/validate CNPJNumeric cnpj)))

(defn is-alfanumeric?
  "Returns true if the cleaned CNPJ is alphanumeric (length 14, last two digits numeric, contains a letter).
  
  Args:
    cnpj - The CNPJ string to check
    
  Returns:
    true if alphanumeric with required shape, false otherwise"
  [cnpj]
  (and (string? cnpj) (m/validate CNPJAlfanumericStrict cnpj)))