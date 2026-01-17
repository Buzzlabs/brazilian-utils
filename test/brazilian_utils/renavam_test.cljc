(ns brazilian-utils.renavam-test
  (:require
   #?(:clj  [clojure.test :refer [deftest is testing]]
      :cljs [cljs.test :refer-macros [deftest is testing]])
   [brazilian-utils.renavam :as renavam]))

(def valid-renavams
  ["12345678900"      ; base 1234567890 -> DV 0
   "123456789"        ; old 9-digit, pads to 00123456789 -> DV 9
   "1234 567 890 0"]) ; formatted with spaces

(def invalid-renavams
  ["12345678901"      ; wrong DV
   "00000000000"      ; repeated digits
   "12345678"         ; too short
   "123456789000"     ; too long
   "" nil "abcdefghi"])

(deftest is-valid?-test
  (testing "accepts valid RENAVAMs"
    (doseq [n valid-renavams]
      (is (true? (renavam/is-valid? n)))))

  (testing "rejects invalid RENAVAMs"
    (doseq [n invalid-renavams]
      (is (false? (renavam/is-valid? n))))))



(deftest remove-symbols-test
  (testing "cleans symbols"
    (is (= "12345678900" (renavam/remove-symbols "1234 567 890-0")))
    (is (= "" (renavam/remove-symbols nil)))))
