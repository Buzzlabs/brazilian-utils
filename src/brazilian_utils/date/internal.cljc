(ns brazilian-utils.date.internal
  "Internal helper functions for date operations."
  (:require [brazilian-utils.date.validation :as validation]
            [brazilian-utils.helpers :as helpers]))

(defn extract-year
  "Safely extracts year from a date string.
  Accepts both ISO (YYYY-MM-DD) and Brazilian (DD/MM/YYYY) formats.
  
  Args:
    date (string): Date in either format
    
  Returns:
    Year as string, or nil if invalid
    
  Examples:
    (extract-year \"2024-01-15\") ;; \"2024\"
    (extract-year \"15/01/2024\") ;; \"2024\"
    (extract-year \"invalid\") ;; nil"
  [date]
  (helpers/safe-call
   #(when-let [normalized (validation/normalize-date date)]
      (when (and (string? normalized) (>= (count normalized) 4))
        (subs normalized 0 4)))
   nil))

(defn validate-and-extract-date-info
  "Validates and extracts date information.
  
  Pure function that normalizes date and extracts year.
  
  Args:
    date - Date string in any supported format
    
  Returns:
    Vector [normalized-date year] if valid, nil otherwise"
  [date]
  (let [normalized-date (validation/normalize-date date)
        year (extract-year date)]
    (when (and normalized-date year)
      [normalized-date year])))

(defn build-holiday-dates-set
  "Creates a set of holiday dates for quick lookup.
  
  Pure function that transforms holiday list into a set.
  
  Args:
    holidays - List of holiday maps with :date key
    
  Returns:
    Set of date strings"
  [holidays]
  (set (map :date holidays)))

(defn build-holiday-name-map
  "Creates a map from date to holiday name.
  
  Pure function that transforms holiday list into a map.
  
  Args:
    holidays - List of holiday maps with :date and :name keys
    
  Returns:
    Map of date -> name"
  [holidays]
  (reduce (fn [acc h]
            (assoc acc (:date h) (:name h)))
          {}
          holidays))
