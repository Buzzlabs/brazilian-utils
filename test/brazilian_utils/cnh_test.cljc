(ns brazilian-utils.cnh-test
  (:require
   #?(:clj  [clojure.test :refer [deftest is testing]]
      :cljs [cljs.test :refer-macros [deftest is testing]])
   [brazilian-utils.cnh :as cnh]))

;; Valid CNHs built from algorithm
(def valid-cnhs
  ["12345678916"     ; base 123456789 with calculated DVs
   "00000000507"])   ; base 000000005 with penalize path

(def invalid-cnhs
  ["12345678910"      ; wrong DV
   "11111111111"      ; repeated digits
   "1234567891"       ; too short
   "123456789160"     ; too long
   "abc123"           ; non-digits
   "" nil])

(deftest is-valid?-test
  (testing "accepts valid CNHs"
    (doseq [n valid-cnhs]
      (is (true? (cnh/is-valid? n)))))

  (testing "rejects invalid CNHs"
    (doseq [n invalid-cnhs]
      (is (false? (cnh/is-valid? n)))))

  (testing "accepts formatted input"
    (is (true? (cnh/is-valid? "123 456 789 16")))
    (is (true? (cnh/is-valid? "000.000.005-07")))))



(deftest remove-symbols-test
  (testing "cleans symbols"
    (is (= "12345678916" (cnh/remove-symbols "123 456 789-16")))
    (is (= "" (cnh/remove-symbols nil)))))
