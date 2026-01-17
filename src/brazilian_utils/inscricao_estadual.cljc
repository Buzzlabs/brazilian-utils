(ns brazilian-utils.inscricao-estadual
  "Brazilian state registration (Inscrição Estadual) validation utilities.
   
   Functions:
     - is-valid?: Validates IE for a given state
     - remove-symbols: Removes formatting characters
   
   Supported states: AC, AL, AP, AM, BA, CE, DF, ES, GO, MA, MG, MT, MS,
                    PA, PB, PE, PI, PR, RJ, RN, RO, RR, RS, SC, SE, SP, TO
   
   Examples:
     (require '[brazilian-utils.inscricao-estadual :as ie])
     (ie/is-valid? :SP \"110042490114\") ;; => true
     (ie/remove-symbols \"11.004.249.0114\") ;; => \"110042490114\""
  (:require [brazilian-utils.inscricao-estadual.core :as core]))

(def is-valid? core/is-valid?)
(def remove-symbols core/remove-symbols)
