(ns brazilian-utils.cnpj
  "CNPJ validation and manipulation utilities.
  
  Functions for validating, formatting, and generating Brazilian CNPJ (Cadastro Nacional da Pessoa Jurídica).
  All functions handle both formatted (XX.XXX.XXX/XXXX-XX) and unformatted (XXXXXXXXXXXXXX) inputs.
  Supports both numeric and alfanumeric CNPJs.
  
  Examples:
    (is-valid? \"12.345.678/0001-95\") ;; => true or false
    (is-valid-alfanumeric? \"12ABC.D34.5EF/G001-95\") ;; => true or false
    (format-cnpj \"12345678000195\") ;; => \"12.345.678/0001-95\"
    (remove-symbols \"12.345.678/0001-95\") ;; => \"12345678000195\"
    (generate) ;; => \"34567890000121\" (random valid CNPJ)"
  (:require [brazilian-utils.cnpj.core :as core]))

(def remove-symbols core/remove-symbols)
(def format-cnpj core/format-cnpj)
(def generate core/generate)
(def generate-alfanumeric core/generate-alfanumeric)
(def is-valid? core/is-valid?)