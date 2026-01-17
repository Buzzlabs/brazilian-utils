(ns brazilian-utils.date.validation
  "Validation functions for date formats."
  (:require [brazilian-utils.helpers :as helpers]))

(defn valid-date-format?
  "Validates if a date string is in the format YYYY-MM-DD.
  
  Args:
    date (string): Date to validate
    
  Returns:
    true if valid format, false otherwise
    
  Examples:
    (valid-date-format? \"2024-01-15\") ;; true
    (valid-date-format? \"2024/01/15\") ;; false
    (valid-date-format? \"15-01-2024\") ;; false
    (valid-date-format? nil) ;; false"
  [date]
  (helpers/validate-string-format date 10 \- [4 7]))

(defn valid-brazilian-date-format?
  "Validates if a date string is in Brazilian format DD/MM/YYYY.
  
  Args:
    date (string): Date to validate
    
  Returns:
    true if valid format, false otherwise
    
  Examples:
    (valid-brazilian-date-format? \"15/01/2024\") ;; true
    (valid-brazilian-date-format? \"2024-01-15\") ;; false"
  [date]
  (helpers/validate-string-format date 10 \/ [2 5]))

(defn brazilian->iso-date
  "Converts Brazilian date format (DD/MM/YYYY) to ISO format (YYYY-MM-DD).
  
  Args:
    date (string): Date in DD/MM/YYYY format
    
  Returns:
    Date in YYYY-MM-DD format, or nil if invalid
    
  Examples:
    (brazilian->iso-date \"25/12/2024\") ;; \"2024-12-25\"
    (brazilian->iso-date \"01/01/2024\") ;; \"2024-01-01\""
  [date]
  (when (valid-brazilian-date-format? date)
    (when-let [{:keys [day month year]} (helpers/extract-date-parts date :brazilian)]
      (str year "-" month "-" day))))

(defn normalize-date
  "Normalizes a date to ISO format (YYYY-MM-DD).
  Accepts both ISO (YYYY-MM-DD) and Brazilian (DD/MM/YYYY) formats.
  
  Args:
    date (string): Date in either format
    
  Returns:
    Date in YYYY-MM-DD format, or nil if invalid
    
  Examples:
    (normalize-date \"2024-12-25\") ;; \"2024-12-25\"
    (normalize-date \"25/12/2024\") ;; \"2024-12-25\""
  [date]
  (cond
    (valid-date-format? date) date
    (valid-brazilian-date-format? date) (brazilian->iso-date date)
    :else nil))

(defn iso->brazilian-date
  "Converts ISO date format (YYYY-MM-DD) to Brazilian format (DD/MM/YYYY).
  
  Args:
    date (string): Date in YYYY-MM-DD format
    
  Returns:
    Date in DD/MM/YYYY format, or nil if invalid
    
  Examples:
    (iso->brazilian-date \"2024-12-25\") ;; \"25/12/2024\"
    (iso->brazilian-date \"2024-01-01\") ;; \"01/01/2024\""
  [date]
  (when (valid-date-format? date)
    (when-let [{:keys [day month year]} (helpers/extract-date-parts date :iso)]
      (str day "/" month "/" year))))
