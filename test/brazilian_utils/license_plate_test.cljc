(ns brazilian-utils.license-plate-test
  (:require [clojure.test :refer [deftest is testing]]
            [brazilian-utils.license-plate :as license-plate]))

;; ============================================================================
;; Test License Plate Validation - is-valid?
;; ============================================================================

(deftest is-valid?-test
  
  (testing "should return false"
    
    (testing "when it is an empty string"
      (is (false? (license-plate/is-valid? ""))))
    
    (testing "when it is null"
      (is (false? (license-plate/is-valid? nil))))
    
    (testing "when it is undefined (doesn't apply to Clojure, testing nil covers this)"
      (is (false? (license-plate/is-valid? nil))))
    
    (testing "when it is a boolean"
      (is (false? (license-plate/is-valid? true)))
      (is (false? (license-plate/is-valid? false))))
    
    (testing "when it is an object"
      (is (false? (license-plate/is-valid? {}))))
    
    (testing "when it is an array"
      (is (false? (license-plate/is-valid? []))))
    
    (testing "when brazilian license plate format is invalid"
      (is (false? (license-plate/is-valid? "abc12345")))
      (is (false? (license-plate/is-valid? "5abc1234")))
      (is (false? (license-plate/is-valid? "abcd1234")))
      (is (false? (license-plate/is-valid? "abcd234"))))
    
    (testing "when Mercosul plate has invalid letters (I, O, Q not allowed in 5th position)"
      (is (false? (license-plate/is-valid? "ABC1I23")))
      (is (false? (license-plate/is-valid? "ABC1O23")))
      (is (false? (license-plate/is-valid? "ABC1Q23")))
      (is (false? (license-plate/is-valid? "abc1i23")))
      (is (false? (license-plate/is-valid? "abc1o23")))
      (is (false? (license-plate/is-valid? "abc1q23")))))
  
  (testing "should return true"
    
    (testing "when brazilian license plate format is valid"
      (is (true? (license-plate/is-valid? "abc1234")))
      (is (true? (license-plate/is-valid? "ABC1234")))
      (is (true? (license-plate/is-valid? "abc-1234")))
      (is (true? (license-plate/is-valid? "ABC-1234"))))
    
    (testing "when mercosul license plate format is valid"
      (is (true? (license-plate/is-valid? "abc1d23")))
      (is (true? (license-plate/is-valid? "ABC1D23")))
      
      ;; Test all valid letters (A-H, J-N, P, R-Z) - excluding I, O, Q
      (is (true? (license-plate/is-valid? "ABC1A23")))
      (is (true? (license-plate/is-valid? "ABC1B23")))
      (is (true? (license-plate/is-valid? "ABC1C23")))
      (is (true? (license-plate/is-valid? "ABC1H23")))
      (is (true? (license-plate/is-valid? "ABC1J23")))
      (is (true? (license-plate/is-valid? "ABC1K23")))
      (is (true? (license-plate/is-valid? "ABC1P23")))
      (is (true? (license-plate/is-valid? "ABC1Z23")))
      
      ;; Explicitly confirm I, O, Q do not work
      (is (false? (license-plate/is-valid? "ABC1I23")))
      (is (false? (license-plate/is-valid? "ABC1O23")))
      (is (false? (license-plate/is-valid? "ABC1Q23"))))))

;; ============================================================================
;; Test License Plate Format Detection - get-format
;; ============================================================================

(deftest get-format-test
  
  (testing "should return \"LLLNNNN\" for traditional format"
    (is (= "LLLNNNN" (license-plate/get-format "ABC1234")))
    (is (= "LLLNNNN" (license-plate/get-format "abc1234")))
    (is (= "LLLNNNN" (license-plate/get-format "ABC-1234")))
    (is (= "LLLNNNN" (license-plate/get-format "abc-1234"))))
  
  (testing "should return \"LLLNLNN\" for Mercosul format"
    (is (= "LLLNLNN" (license-plate/get-format "ABC1D23")))
    (is (= "LLLNLNN" (license-plate/get-format "abc1d23"))))
  
  (testing "should return nil for invalid formats"
    (is (nil? (license-plate/get-format "")))
    (is (nil? (license-plate/get-format nil)))
    (is (nil? (license-plate/get-format "invalid")))
    (is (nil? (license-plate/get-format "ABCD123")))
    (is (nil? (license-plate/get-format "ABC123")))
    (is (nil? (license-plate/get-format "12ABC34")))
    (is (nil? (license-plate/get-format "ABC1234567")))))

;; ============================================================================
;; Test License Plate Mercosul Conversion - convert-to-mercosul
;; ============================================================================

(deftest convert-to-mercosul-test
  
  (testing "should convert traditional to Mercosul format correctly"
    ;; The conversion uses the second digit and converts to letter
    ;; Second digit index: ABC[1234] -> second digit is 2 -> ASCII 65+2 = 67 = 'C'
    (is (= "ABC1C34" (license-plate/convert-to-mercosul "ABC1234")))
    (is (= "ABC1C34" (license-plate/convert-to-mercosul "abc1234")))
    (is (= "ABC1C34" (license-plate/convert-to-mercosul "ABC-1234")))
    (is (= "ABC1C34" (license-plate/convert-to-mercosul "abc-1234")))
    
    ;; Test with different second digits (0-9)
    ;; DEF[1056]: second digit = 0 -> 65+0 = 65 = 'A'
    (is (= "DEF1A56" (license-plate/convert-to-mercosul "DEF1056")))
    ;; GHI[1278]: second digit = 2 -> 65+2 = 67 = 'C'
    (is (= "GHI1C78" (license-plate/convert-to-mercosul "GHI1278")))
    ;; JKL[1390]: second digit = 3 -> 65+3 = 68 = 'D'
    (is (= "JKL1D90" (license-plate/convert-to-mercosul "JKL1390")))
    ;; MNO[1412]: second digit = 4 -> 65+4 = 69 = 'E'
    (is (= "MNO1E12" (license-plate/convert-to-mercosul "MNO1412")))
    ;; PQR[1534]: second digit = 5 -> 65+5 = 70 = 'F'
    (is (= "PQR1F34" (license-plate/convert-to-mercosul "PQR1534")))
    ;; STU[1656]: second digit = 6 -> 65+6 = 71 = 'G'
    (is (= "STU1G56" (license-plate/convert-to-mercosul "STU1656")))
    ;; VWX[1778]: second digit = 7 -> 65+7 = 72 = 'H'
    (is (= "VWX1H78" (license-plate/convert-to-mercosul "VWX1778")))
    ;; YZA[1890]: second digit = 8 -> 65+8 = 73 = 'I'
    (is (= "YZA1I90" (license-plate/convert-to-mercosul "YZA1890")))
    ;; BCD[1912]: second digit = 9 -> 65+9 = 74 = 'J'
    (is (= "BCD1J12" (license-plate/convert-to-mercosul "BCD1912"))))
  
  (testing "should return nil for invalid inputs"
    (is (nil? (license-plate/convert-to-mercosul "")))
    (is (nil? (license-plate/convert-to-mercosul nil)))
    (is (nil? (license-plate/convert-to-mercosul "invalid")))
    (is (nil? (license-plate/convert-to-mercosul "ABCD123"))) ;; 4 letters
    (is (nil? (license-plate/convert-to-mercosul "AB1234"))) ;; 2 letters
    (is (nil? (license-plate/convert-to-mercosul "ABC123"))) ;; 3 digits
    (is (nil? (license-plate/convert-to-mercosul "ABC12345")))) ;; 5 digits
  
  (testing "should return nil when plate is already Mercosul format"
    (is (nil? (license-plate/convert-to-mercosul "ABC1D23")))
    (is (nil? (license-plate/convert-to-mercosul "abc1d23")))
    (is (nil? (license-plate/convert-to-mercosul "XYZ1K99")))))
