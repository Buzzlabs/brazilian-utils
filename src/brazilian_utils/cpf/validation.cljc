(ns brazilian-utils.cpf.validation
  (:require [malli.core :as m]))

(def CPFFormatted
  "Schema for formatted numeric CPF: XXX.XXX.XXX-XX"
  [:re #"^\d{3}\.\d{3}\.\d{3}-\d{2}$"])

(def CPFWithOptionalFormatting
  "Schema for CPF with optional formatting"
  [:re #"^\d{3}\.?\d{3}\.?\d{3}-?\d{2}$"])

(defn is-formatted?
  "Checks if CPF is formatted (XXX.XXX.XXX-XX).
  
  Args:
    cpf - The CPF string to check
    
  Returns:
  true if formatted with mask, false otherwise"
  [cpf]
  (and (string? cpf) (m/validate CPFFormatted cpf)))

(defn is-valid-format?
  "Checks if CPF matches expected format (with or without formatting).
  
  Args:
    cpf - The CPF string to check
    
  Returns:
    true if format is valid, false otherwise"
  [cpf]
  (and (string? cpf) (m/validate CPFWithOptionalFormatting cpf)))
