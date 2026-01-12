(ns brazilian-utils.capitalize
  "String capitalization utilities.
  
  Functions for capitalizing strings with support for Portuguese prepositions,
  conjunctions, and custom word rules.
  
  Examples:
    (capitalize \"esponja de aço\") ;; => \"Esponja de Aço\"
    (capitalize \"josé ama maria\" {:lower-case-words [\"ama\"]}) ;; => \"José ama Maria\""
  (:require [brazilian-utils.capitalize.core :as core]))

(def capitalize core/capitalize)
