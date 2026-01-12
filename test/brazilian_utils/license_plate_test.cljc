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
      (is (false? (license-plate/is-valid? "abcd234")))))
  
  (testing "should return true"
    
    (testing "when brazilian license plate format is valid"
      (is (true? (license-plate/is-valid? "abc1234")))
      (is (true? (license-plate/is-valid? "ABC1234")))
      (is (true? (license-plate/is-valid? "abc-1234")))
      (is (true? (license-plate/is-valid? "ABC-1234"))))
    
    (testing "when mercosul license plate format is valid"
      (is (true? (license-plate/is-valid? "abc1d23")))
      (is (true? (license-plate/is-valid? "ABC1D23"))))))
