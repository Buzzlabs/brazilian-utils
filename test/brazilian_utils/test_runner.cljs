(ns brazilian-utils.test-runner
  (:require
   ;; Core tests
   [brazilian-utils.boleto-test]
   [brazilian-utils.capitalize-test]
   [brazilian-utils.cep-test]
   [brazilian-utils.cities-test]
   [brazilian-utils.cnpj-test]
   [brazilian-utils.cpf-test]
   [brazilian-utils.cnh-test]
   [brazilian-utils.currency-test]
   [brazilian-utils.email-test]
   [brazilian-utils.helpers-test]
   [brazilian-utils.inscricao-estadual-test]
   [brazilian-utils.license-plate-test]
   [brazilian-utils.phone-test]
   [brazilian-utils.pis-test]
   [brazilian-utils.processo-juridico-test]
   [brazilian-utils.renavam-test]
   [brazilian-utils.titulo-eleitoral-test]
   [brazilian-utils.states-test]
   [clojure.test]))

;; This file ensures all test namespaces are loaded for ClojureScript test runner
(println "Test runner loaded, tests should run now")
