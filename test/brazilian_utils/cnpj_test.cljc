(ns brazilian-utils.cnpj-test
  (:require #?(:clj [clojure.test :refer :all]
               :cljs [cljs.test :refer-macros [deftest is testing]])
            [brazilian-utils.cnpj :as cnpj]
            [brazilian-utils.cnpj.validation :as validation]
            [brazilian-utils.cnpj.internal :as i]))

(def cnpj-length 14)

;; ============================================================================
;; Test format-cnpj
;; ============================================================================

(deftest test-format-cnpj
  (testing "should format cnpj with mask"
    (is (= "" (cnpj/format-cnpj "")))
    (is (= "4" (cnpj/format-cnpj "4")))
    (is (= "46" (cnpj/format-cnpj "46")))
    (is (= "46.8" (cnpj/format-cnpj "468")))
    (is (= "46.84" (cnpj/format-cnpj "4684")))
    (is (= "46.843" (cnpj/format-cnpj "46843")))
    (is (= "46.843.4" (cnpj/format-cnpj "468434")))
    (is (= "46.843.48" (cnpj/format-cnpj "4684348")))
    (is (= "46.843.485" (cnpj/format-cnpj "46843485")))
    (is (= "46.843.485/0" (cnpj/format-cnpj "468434850")))
    (is (= "46.843.485/00" (cnpj/format-cnpj "4684348500")))
    (is (= "46.843.485/000" (cnpj/format-cnpj "46843485000")))
    (is (= "46.843.485/0001" (cnpj/format-cnpj "468434850001")))
    (is (= "46.843.485/0001-8" (cnpj/format-cnpj "4684348500018")))
    (is (= "46.843.485/0001-86" (cnpj/format-cnpj "46843485000186"))))

  (testing "should format number cnpj with mask"
    (is (= "4" (cnpj/format-cnpj 4)))
    (is (= "46" (cnpj/format-cnpj 46)))
    (is (= "46.8" (cnpj/format-cnpj 468)))
    (is (= "46.84" (cnpj/format-cnpj 4684)))
    (is (= "46.843" (cnpj/format-cnpj 46843)))
    (is (= "46.843.4" (cnpj/format-cnpj 468434)))
    (is (= "46.843.48" (cnpj/format-cnpj 4684348)))
    (is (= "46.843.485" (cnpj/format-cnpj 46843485)))
    (is (= "46.843.485/0" (cnpj/format-cnpj 468434850)))
    (is (= "46.843.485/00" (cnpj/format-cnpj 4684348500)))
    (is (= "46.843.485/000" (cnpj/format-cnpj 46843485000)))
    (is (= "46.843.485/0001" (cnpj/format-cnpj 468434850001)))
    (is (= "46.843.485/0001-8" (cnpj/format-cnpj 4684348500018)))
    (is (= "46.843.485/0001-86" (cnpj/format-cnpj 46843485000186))))

  (testing "should format cnpj with mask filling zeroes"
    (is (= "00.000.000/0000-00" (cnpj/format-cnpj "" {:pad true})))
    (is (= "00.000.000/0000-04" (cnpj/format-cnpj "4" {:pad true})))
    (is (= "00.000.000/0000-46" (cnpj/format-cnpj "46" {:pad true})))
    (is (= "00.000.000/0004-68" (cnpj/format-cnpj "468" {:pad true})))
    (is (= "00.000.000/0046-84" (cnpj/format-cnpj "4684" {:pad true})))
    (is (= "00.000.000/0468-43" (cnpj/format-cnpj "46843" {:pad true})))
    (is (= "00.000.000/4684-34" (cnpj/format-cnpj "468434" {:pad true})))
    (is (= "00.000.004/6843-48" (cnpj/format-cnpj "4684348" {:pad true})))
    (is (= "00.000.046/8434-85" (cnpj/format-cnpj "46843485" {:pad true})))
    (is (= "00.000.468/4348-50" (cnpj/format-cnpj "468434850" {:pad true})))
    (is (= "00.004.684/3485-00" (cnpj/format-cnpj "4684348500" {:pad true})))
    (is (= "00.046.843/4850-00" (cnpj/format-cnpj "46843485000" {:pad true})))
    (is (= "00.468.434/8500-01" (cnpj/format-cnpj "468434850001" {:pad true})))
    (is (= "04.684.348/5000-18" (cnpj/format-cnpj "4684348500018" {:pad true})))
    (is (= "46.843.485/0001-86" (cnpj/format-cnpj "46843485000186" {:pad true}))))

  (testing "should NOT add digits after the CNPJ length"
    (is (= "46.843.485/0001-86" (cnpj/format-cnpj "468434850001860000000000"))))

  (testing "should remove all non alphanumeric characters"
    (is (= "46.ABC.843/4850-00" (cnpj/format-cnpj "46.?ABC843.485/0001-86abc"))))

  (testing "should format alphanumeric cnpj with mask"
    (is (= "AB.1C2.D3E/4F5G-6" (cnpj/format-cnpj "AB1C2D3E4F5G6")))
    (is (= "12.ABC.345/01DE-35" (cnpj/format-cnpj "12ABC34501DE35")))
    (is (= "AB.CDE.FGH/IJKL-35" (cnpj/format-cnpj "ABCDEFGHIJKL35")))))

;; ============================================================================
;; Test generate-cnpj 
;; ============================================================================

(deftest test-generate-cnpj
  (testing "should have the right length without mask"
    (is (= cnpj-length (count (cnpj/generate)))))

  (testing "should return valid CNPJ - comprehensive validation"
    (dotimes [_ 100]
      (let [generated (cnpj/generate)]
        (is (cnpj/is-valid? generated)))))

  (testing "generated CNPJs should be formatted correctly"
    (dotimes [_ 10]
      (let [generated (cnpj/generate)
            formatted (cnpj/format-cnpj generated)]
        (is (cnpj/is-valid? formatted))
        (is (re-matches #"^\d{2}\.\d{3}\.\d{3}[/]\d{4}-\d{2}$" formatted)))))

  (testing "generated CNPJs should not be reserved numbers"
    (dotimes [_ 50]
      (let [generated (cnpj/generate)]
        (is (not (re-matches #"^(\d)\1{13}$" generated)))))))

;; ============================================================================
;; Test remove-symbols
;; ============================================================================

(deftest test-remove-symbols
  (testing "should remove special characters and convert to uppercase"
    (is (= "12ABC34501DE35" (cnpj/remove-symbols "12.ABC.345/01DE-35")))
    (is (= "12345678000195" (cnpj/remove-symbols "12.345.678/0001-95")))
    (is (= "ABCDEFGHIJKL35" (cnpj/remove-symbols "ab.cde.fgh/ijkl-35")))
    (is (= "12ABC34501DE35ABC" (cnpj/remove-symbols "12.?ABC.345/01DE-35abc"))))

  (testing "handles edge cases"
    (is (= "" (cnpj/remove-symbols "")))
    (is (= "" (cnpj/remove-symbols nil)))
    (is (= "" (cnpj/remove-symbols "   ")))
    (is (= "" (cnpj/remove-symbols "!@#$%^&*()")))))

;; ============================================================================
;; Test is-valid?
;; ============================================================================

(deftest test-is-valid
  (testing "should return false for reserved numbers"
    (is (false? (cnpj/is-valid? "00000000000000")))
    (is (false? (cnpj/is-valid? "11111111111111")))
    (is (false? (cnpj/is-valid? "22222222222222")))
    (is (false? (cnpj/is-valid? "33333333333333")))
    (is (false? (cnpj/is-valid? "44444444444444")))
    (is (false? (cnpj/is-valid? "55555555555555")))
    (is (false? (cnpj/is-valid? "66666666666666")))
    (is (false? (cnpj/is-valid? "77777777777777")))
    (is (false? (cnpj/is-valid? "88888888888888")))
    (is (false? (cnpj/is-valid? "99999999999999")))
    (is (false? (cnpj/is-valid? "00.000.000/0000-00")))
    (is (false? (cnpj/is-valid? "11.111.111/1111-11"))))

  (testing "should return false for invalid inputs"
    (is (not (cnpj/is-valid? "")))
    (is (not (cnpj/is-valid? nil)))
    (is (not (cnpj/is-valid? true)))
    (is (not (cnpj/is-valid? false)))
    (is (not (cnpj/is-valid? {})))
    (is (not (cnpj/is-valid? [])))
    (is (not (cnpj/is-valid? "12312312312")))
    (is (not (cnpj/is-valid? "ababcabcabcdab")))
    (is (not (cnpj/is-valid? "6ad0.t391.9asd47/0ad001-00")))
    (is (not (cnpj/is-valid? "11257245286531"))))

  (testing "should return true for valid CNPJ"
    (is (cnpj/is-valid? "13723705000189"))
    (is (cnpj/is-valid? "60.391.947/0001-00")))

  (testing "should return true for valid generated CNPJs"
    (let [numeric-cnpj (cnpj/generate)]
      (is (cnpj/is-valid? numeric-cnpj))))

  (testing "returns false for invalid CNPJs"
    (is (false? (cnpj/is-valid? "1144477700016")))
    (is (false? (cnpj/is-valid? "114447770001610")))
    (is (false? (cnpj/is-valid? "11.444.777/0001-60")))
    (is (false? (cnpj/is-valid? "11444777000160")))
    (is (false? (cnpj/is-valid? "abcdefghijklmn")))
    (is (false? (cnpj/is-valid? "")))
    (is (false? (cnpj/is-valid? nil))))

  (testing "handles different types"
    (is (false? (cnpj/is-valid? 123)))
    (is (false? (cnpj/is-valid? [])))
    (is (false? (cnpj/is-valid? {})))))

;; ============================================================================
;; Test is-formatted-cnpj?
;; ============================================================================

(deftest test-is-formatted-cnpj
  (testing "should return true for correctly formatted CNPJs"
    (is (validation/is-formatted? "12.345.678/0001-95"))
    (is (validation/is-formatted? "11.444.777/0001-61"))
    (is (validation/is-formatted? "00.000.000/0001-91")))
  
  (testing "should return false for unformatted CNPJs"
    (is (not (validation/is-formatted? "12345678000195")))
    (is (not (validation/is-formatted? "11444777000161"))))
  
  (testing "should return false for partially formatted CNPJs"
    (is (not (validation/is-formatted? "12.345.678/0001-9")))
    (is (not (validation/is-formatted? "12.345.678/001-95")))
    (is (not (validation/is-formatted? "2.345.678/0001-95")))
    (is (not (validation/is-formatted? "12.345.67/0001-95")))
    (is (not (validation/is-formatted? "12.345.678/0001")))
    (is (not (validation/is-formatted? "12.345.678-0001-95"))))
  
  (testing "should return false for CNPJs with letters (alphanumeric)"
    (is (not (validation/is-formatted? "AB.CDE.FGH/IJKL-35")))
    (is (not (validation/is-formatted? "1A.345.678/0001-95")))
    (is (not (validation/is-formatted? "12.3B5.678/0001-95"))))
  
  (testing "should return false for invalid inputs"
    (is (not (validation/is-formatted? nil)))
    (is (not (validation/is-formatted? "")))
    (is (not (validation/is-formatted? "invalid")))
    (is (not (validation/is-formatted? "12.345.678/0001-95-extra")))
    (is (not (validation/is-formatted? "  12.345.678/0001-95  ")))))
  
  (testing "should return false for wrong format patterns"
    (is (not (validation/is-formatted? "123.45.678/0001-95")))
    (is (not (validation/is-formatted? "12.3456.78/0001-95")))
    (is (not (validation/is-formatted? "12.345.678\\0001-95")))
    (is (not (validation/is-formatted? "12/345/678.0001-95")))
    (is (not (validation/is-formatted? "12.345.678/0001_95"))))

;; ============================================================================
;; Test CNPJ Alfanumérico
;; ============================================================================

(deftest test-generate-cnpj-alfanumeric
  (testing "should have the right length without mask"
    (is (= cnpj-length (count (cnpj/generate-alfanumeric)))))

  (testing "should return valid alphanumeric CNPJ"
    (dotimes [_ 50]
      (let [generated (cnpj/generate-alfanumeric)]
        (is (cnpj/is-valid? generated)))))

  (testing "generated alphanumeric CNPJs should be formatted correctly"
    (dotimes [_ 10]
      (let [generated (cnpj/generate-alfanumeric)
            formatted (cnpj/format-cnpj generated)]
        (is (cnpj/is-valid? formatted))
        (is (re-matches #"^[0-9A-Z]{2}\.[0-9A-Z]{3}\.[0-9A-Z]{3}[/][0-9A-Z]{4}-\d{2}$" formatted)))))

  (testing "alphanumeric CNPJ check digits are always numeric"
    (dotimes [_ 30]
      (let [generated (cnpj/generate-alfanumeric)]
        (is (re-matches #"[0-9]" (subs generated 12 13)))
        (is (re-matches #"[0-9]" (subs generated 13 14)))))))

(deftest test-format-cnpj-alfanumeric
  (testing "should format alphanumeric CNPJ with mask"
    (is (= "AB.12C.3D4/EFG0-19" (cnpj/format-cnpj "AB12C3D4EFG019")))
    (is (= "12.ABC.34D/EF01-95" (cnpj/format-cnpj "12ABC34DEF0195"))))

  (testing "should handle alphanumeric CNPJ with lowercase"
    (is (= "AB.12C.3D4/EFG0-19" (cnpj/format-cnpj "ab12c3d4efg019")))
    (is (= "12.ABC.34D/EF01-95" (cnpj/format-cnpj "12abc34def0195"))))

  (testing "should NOT add digits after CNPJ length"
    (is (= "AB.12C.3D4/EFG0-19" (cnpj/format-cnpj "AB12C3D4EFG0190000000000"))))

  (testing "should format partially filled alphanumeric CNPJ"
    (is (= "A" (cnpj/format-cnpj "A")))
    (is (= "AB.1" (cnpj/format-cnpj "AB1")))
    (is (= "AB.12C.3" (cnpj/format-cnpj "AB12C3")))))

(deftest test-is-valid-alfanumeric
  (testing "should return true for valid alphanumeric CNPJs"
    (let [cnpj (cnpj/generate-alfanumeric)]
      (is (cnpj/is-valid? cnpj))))

  (testing "should return false for invalid check digits"
    (is (not (cnpj/is-valid? "AB12C3D4EFG000")))
    (is (not (cnpj/is-valid? "AB12C3D4EFG010"))))

  (testing "should return false when DVs contain letters"
    (is (not (cnpj/is-valid? "AB12C3D4EFG0A9")))
    (is (not (cnpj/is-valid? "AB12C3D4EFG01Z"))))

  (testing "should return false for wrong length"
    (is (not (cnpj/is-valid? "AB12C3D4EFG01")))
    (is (not (cnpj/is-valid? "AB12C3D4EFG0195"))))

  (testing "should handle formatted input"
    (let [generated (cnpj/generate-alfanumeric)
          formatted (cnpj/format-cnpj generated)]
      (is (cnpj/is-valid? formatted))))

  (testing "should return false for invalid inputs"
    (is (not (cnpj/is-valid? "")))
    (is (not (cnpj/is-valid? nil)))
    (is (not (cnpj/is-valid? true)))
    (is (not (cnpj/is-valid? false)))
    (is (not (cnpj/is-valid? {})))
    (is (not (cnpj/is-valid? [])))))

(deftest test-is-formatted-cnpj-alfanumeric
  (testing "should return true for correctly formatted alphanumeric CNPJs"
    (is (validation/is-formatted-alfanumeric? "AB.12C.3D4/EFG0-19"))
    (is (validation/is-formatted-alfanumeric? "12.ABC.34D/EF01-95")))

  (testing "should return false for unformatted alphanumeric CNPJs"
    (is (not (validation/is-formatted-alfanumeric? "AB12C3D4EFG019")))
    (is (not (validation/is-formatted-alfanumeric? "12ABC34DEF0195"))))

  (testing "should return false for invalid inputs"
    (is (not (validation/is-formatted-alfanumeric? nil)))
    (is (not (validation/is-formatted-alfanumeric? "")))
    (is (not (validation/is-formatted-alfanumeric? "invalid")))))

(deftest test-valid-format
  (testing "mascarado válido (numérico)"
    (is (validation/is-formatted? "12.345.678/0001-95")))
  (testing "mascarado válido (alfanumérico)"
    (is (validation/is-formatted-alfanumeric? "12.ABC.345/01DE-35"))
  (testing "sem máscara (aproximação do isValidFormat do JS)"
    (is (boolean (re-matches #"^\d{14}$" "12345678000195")))
    (is (boolean (re-matches #"^[0-9A-Z]{14}$" "12ABC34501DE35"))))))

(deftest test-is-numeric-cnpj
  (testing "numeric CNPJs"
    (is (true? (validation/is-numeric? (cnpj/remove-symbols "12345678000195"))))
    (is (true? (validation/is-numeric? (cnpj/remove-symbols "12.345.678/0001-95"))))
    (is (true? (validation/is-numeric? (cnpj/remove-symbols "00000000000000")))))
  (testing "alphanumeric CNPJs"
    (is (false? (validation/is-numeric? (cnpj/remove-symbols "12ABC34501DE35"))))
    (is (false? (validation/is-numeric? (cnpj/remove-symbols "AB.1C2.D3E/4F5G-35"))))))

(deftest test-is-alfanumeric-cnpj
  (testing "alphanumeric CNPJs"
    (is (true? (validation/is-alfanumeric? (cnpj/remove-symbols "12ABC34501DE35"))))
    (is (true? (validation/is-alfanumeric? (cnpj/remove-symbols "AB.1C2.D3E/4F5G-35")))))
  (testing "numeric CNPJs"
    (is (false? (validation/is-alfanumeric? (cnpj/remove-symbols "12345678000195"))))
    (is (false? (validation/is-alfanumeric? (cnpj/remove-symbols "12.345.678/0001-95"))))))

(deftest test-char-to-cnpj-value
  (testing "internal char mapping (documentar diferença vs suite JS)"
    (is (= 10 (i/char->cnpj-value "A")))
    (is (= 11 (i/char->cnpj-value "B")))
    (is (= 0 (i/char->cnpj-value "0")))
    (is (= 9 (i/char->cnpj-value "9")))))
