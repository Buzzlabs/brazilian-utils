(ns brazilian-utils.cpf
  "CPF validation and manipulation utilities.
  
  Functions for validating, formatting, and generating Brazilian CPF (Cadastro de Pessoas Físicas).
  All functions handle both formatted (XXX.XXX.XXX-XX) and unformatted (XXXXXXXXXXX) inputs.
  
  Examples:
    (is-valid? \"123.456.789-09\") ;; => true or false
    (format-cpf \"12345678909\") ;; => \"123.456.789-09\"
    (clean \"123.456.789-09\") ;; => \"12345678909\"
    (generate) ;; => \"34567890120\" (random valid CPF)
    (generate :SP) ;; => \"34567890121\" (with state code)"
  (:require [brazilian-utils.cpf.core :as core]))

(def clean core/clean)
(def format-cpf core/format-cpf)
(def generate core/generate)
(def is-valid? core/is-valid?)