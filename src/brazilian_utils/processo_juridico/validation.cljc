(ns brazilian-utils.processo-juridico.validation
  (:require [malli.core :as m]))

(def ProcessoFormatted
  "Schema for formatted Processo Jurídico: NNNNNNN-DD.AAAA.J.TT.OOOO"
  [:re #"^\d{7}-\d{2}\.\d{4}\.\d{1}\.\d{2}\.\d{4}$"])

(def ProcessoWithOptionalFormatting
  "Schema for Processo with optional formatting"
  [:re #"^\d{7}-?\d{2}\.?\d{4}\.?\d{1}\.?\d{2}\.?\d{4}$"])

(defn is-formatted?
  "Checks if Processo Jurídico is formatted (NNNNNNN-DD.AAAA.J.TT.OOOO).
  
  Args:
    processo - The Processo Jurídico string to check
    
  Returns:
    true if formatted with mask, false otherwise"
  [processo]
  (and (string? processo) (m/validate ProcessoFormatted processo)))

(defn is-valid-format?
  "Checks if Processo matches expected format (with or without formatting).
  
  Args:
    processo - The Processo Jurídico string to check
    
  Returns:
    true if format is valid, false otherwise"
  [processo]
  (and (string? processo) (m/validate ProcessoWithOptionalFormatting processo)))
