(ns brazilian-utils.cep.schemas
  "Schemas de validação para CEP (Código de Endereçamento Postal).
   
   Utiliza malli para definir e validar estruturas de dados
   relacionadas a CEPs brasileiros."
  (:require [malli.core :as m]))

;; ============================================================================
;; CEP Schemas
;; ============================================================================

(def CEPNumber
  "Schema para CEP sem formatação (8 dígitos numéricos).
   
   Exemplo:
   (m/validate CEPNumber \"01310100\") ;; true
   (m/validate CEPNumber \"0131010\")  ;; false (menos de 8)
   (m/validate CEPNumber \"013101000\") ;; false (mais de 8)"
  [:re #"^\d{8}$"])

(def CEPFormatted
  "Schema para CEP formatado (xxxxx-xxx).
   
   Exemplo:
   (m/validate CEPFormatted \"01310-100\") ;; true
   (m/validate CEPFormatted \"01310100\")  ;; false"
  [:re #"^\d{5}-\d{3}$"])

(defn validate-cep
  "Valida se uma string é um CEP válido (com ou sem formatação).
   
   Retorna true se válido, false caso contrário.
   
   Exemplo:
   (validate-cep \"01310100\")  ;; true
   (validate-cep \"01310-100\") ;; true
   (validate-cep \"0131010\")   ;; false"
  [value]
  (or (m/validate CEPNumber value)
      (m/validate CEPFormatted value)))
