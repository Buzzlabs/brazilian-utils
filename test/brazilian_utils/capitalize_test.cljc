(ns brazilian-utils.capitalize-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [brazilian-utils.capitalize :as capitalize]))

;; ============================================================================
;; Test capitalize
;; ============================================================================

(deftest test-capitalize
  (testing "should capitalize"
    (testing "when the value does not contain preposition"
      (is (= "Esponja Vegetal" (capitalize/capitalize "esponja vegetal")))
      (is (= "Refrigerante 1l" (capitalize/capitalize "refrigerante 1L")))
      (is (= "Joaquim José" (capitalize/capitalize "JOAQUIM JOSÉ"))))

    (testing "when the value does contain preposition"
      (is (= "Esponja de Aço 60g" (capitalize/capitalize "esponja DE aço 60G")))
      (is (= "Fulano de Tal" (capitalize/capitalize "fulano de tal")))
      (is (= "Pão com Manteiga" (capitalize/capitalize "pão com manteiga"))))

    (testing "when the value does contain short words"
      (is (= "A" (capitalize/capitalize "a")))
      (is (= "A B C" (capitalize/capitalize "A B C"))))

    (testing "when the value does contain empty spaces"
      (is (= "" (capitalize/capitalize "")))
      (is (= "" (capitalize/capitalize " ")))
      (is (= "Esponja de Aço 60g" (capitalize/capitalize "esponja de    aço 60G")))
      (is (= "Refrigerante 1l" (capitalize/capitalize "  refrigerante 1l"))))

    (testing "when the value does contain upper case words"
      (is (= "DOC da Empresa AB" (capitalize/capitalize "doc da empresa ab" {:upper-case-words ["DOC" "AB"]})))
      (is (= "DOC Inválido" (capitalize/capitalize "doc inválido" {:upper-case-words ["DOC"]}))))

    (testing "when the value does contain lower case words"
      (is (= "José ama Maria" (capitalize/capitalize "josé Ama MARIA" {:lower-case-words ["ama"]})))
      (is (= "José não ama Maria" (capitalize/capitalize "josé Não Ama MARIA" {:lower-case-words ["não" "ama"]}))))))
