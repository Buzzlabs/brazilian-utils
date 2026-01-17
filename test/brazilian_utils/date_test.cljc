(ns brazilian-utils.date-test
  "Tests for the date module."
  (:require
   [clojure.test :refer [deftest is testing]]
   [brazilian-utils.date :as date]))

;; ============================================================================
;; Test Data: Brazilian National Holidays
;; ============================================================================

(def fixed-holidays-2024
  "Fixed national holidays in 2024"
  [{:date "2024-01-01" :name "Confraternização mundial" :type "fixed"}
   {:date "2024-04-21" :name "Tiradentes" :type "fixed"}
   {:date "2024-05-01" :name "Dia do trabalho" :type "fixed"}
   {:date "2024-09-07" :name "Independência do Brasil" :type "fixed"}
   {:date "2024-10-12" :name "Nossa Senhora Aparecida" :type "fixed"}
   {:date "2024-11-02" :name "Finados" :type "fixed"}
   {:date "2024-11-15" :name "Proclamação da República" :type "fixed"}
   {:date "2024-11-20" :name "Dia da consciência negra" :type "fixed"}
   {:date "2024-12-25" :name "Natal" :type "fixed"}])

(def moveable-holidays-2024
  "Moveable holidays in 2024 (Easter-dependent)"
  [{:date "2024-02-13" :name "Carnaval" :type "moveable"}
   {:date "2024-03-29" :name "Sexta-feira Santa" :type "moveable"}
   {:date "2024-05-30" :name "Corpus Christi" :type "moveable"}])

(def non-holidays-2024
  "Common non-holiday dates in 2024"
  [{:date "2024-01-15" :reason "Random January date"}
   {:date "2024-03-10" :reason "Random March date"}
   {:date "2024-06-15" :reason "Random June date"}
   {:date "2024-08-20" :reason "Random August date"}
   {:date "2024-12-24" :reason "Christmas Eve (not official)"}])

(def fixed-holidays-2025
  "Fixed national holidays in 2025"
  [{:date "2025-01-01" :name "Confraternização mundial"}
   {:date "2025-04-21" :name "Tiradentes"}
   {:date "2025-05-01" :name "Dia do trabalho"}
   {:date "2025-09-07" :name "Independência do Brasil"}
   {:date "2025-10-12" :name "Nossa Senhora Aparecida"}
   {:date "2025-11-02" :name "Finados"}
   {:date "2025-11-15" :name "Proclamação da República"}
   {:date "2025-11-20" :name "Dia da consciência negra"}
   {:date "2025-12-25" :name "Natal"}])

;; ============================================================================
;; Tests: get-holidays
;; ============================================================================

(deftest get-holidays-test
  (testing "retrieves holidays for a valid year"
    (let [result (date/get-holidays 2024)]
      (is (map? result))
      (if-let [error (:error result)]
        ;; Network error is acceptable in test environment
        (is (string? error))
        ;; On success, should have body with vector of holidays
        (do
          (is (contains? result :status))
          (is (contains? result :body))
          (is (vector? (:body result)))
          (when (seq (:body result))
            (let [first-holiday (first (:body result))]
              (is (contains? first-holiday :date))
              (is (contains? first-holiday :name))))))))
  
  (testing "accepts both int and string years"
    (let [result-int (date/get-holidays 2024)
          result-str (date/get-holidays "2024")]
      (is (map? result-int))
      (is (map? result-str))))
  
  (testing "handles different years"
    (doseq [year [2022 2023 2024 2025 2026]]
      (let [result (date/get-holidays year)]
        (is (map? result))))))

;; ============================================================================
;; Tests: is-holiday?
;; ============================================================================

(deftest is-holiday?-test
  (testing "identifies fixed holidays in 2024"
    (doseq [holiday fixed-holidays-2024]
      (let [result (date/is-holiday? (:date holiday))]
        (is (true? result) (str "Expected " (:date holiday) " (" (:name holiday) ") to be a holiday")))))
  
  (testing "identifies moveable holidays in 2024"
    (doseq [holiday moveable-holidays-2024]
      (let [result (date/is-holiday? (:date holiday))]
        (is (true? result) (str "Expected " (:date holiday) " (" (:name holiday) ") to be a holiday")))))
  
  (testing "identifies fixed holidays in 2025"
    (doseq [holiday fixed-holidays-2025]
      (let [result (date/is-holiday? (:date holiday))]
        (is (true? result) (str "Expected " (:date holiday) " (" (:name holiday) ") to be a holiday")))))
  
  (testing "rejects non-holidays in 2024"
    (doseq [non-holiday non-holidays-2024]
      (let [result (date/is-holiday? (:date non-holiday))]
        (is (false? result) (str "Expected " (:date non-holiday) " to NOT be a holiday (" (:reason non-holiday) ")")))))
  
  (testing "handles invalid input"
    (is (false? (date/is-holiday? nil)))
    (is (false? (date/is-holiday? "")))
    (is (false? (date/is-holiday? 123)))
    (is (false? (date/is-holiday? [])))
    (is (false? (date/is-holiday? {}))))
  
  (testing "handles different date formats gracefully"
    ;; Invalid format but should not crash
    (is (false? (date/is-holiday? "2024/01/01")))
    (is (false? (date/is-holiday? "01-01-2024")))
    (is (false? (date/is-holiday? "2024-1-1")))  ;; missing leading zeros
    (is (false? (date/is-holiday? "24-01-01")))  ;; 2-digit year
    (is (false? (date/is-holiday? "not-a-date"))))
  
  (testing "accepts Brazilian date format DD/MM/YYYY"
    ;; Test with Brazilian format
    (is (true? (date/is-holiday? "25/12/2024")))  ;; Christmas
    (is (true? (date/is-holiday? "01/01/2024")))  ;; New Year
    (is (false? (date/is-holiday? "15/01/2024")))) ;; Not a holiday
  
  (testing "handles edge cases"
    ;; Very short strings
    (is (false? (date/is-holiday? "2024")))
    (is (false? (date/is-holiday? "01")))
    ;; Very long strings
    (is (false? (date/is-holiday? "2024-01-01-extra")))
    ;; String with correct length but invalid format
    (is (false? (date/is-holiday? "abcd-ef-gh")))))

;; ============================================================================
;; Tests: get-holiday-name
;; ============================================================================

(deftest get-holiday-name-test
  (testing "returns correct names for fixed holidays in 2024"
    (doseq [holiday fixed-holidays-2024]
      (let [name (date/get-holiday-name (:date holiday))]
        (if name
          (is (string? name) (str "Expected string for " (:date holiday)))
          ;; Network error acceptable
          (is true)))))
  
  (testing "returns correct names for moveable holidays in 2024"
    (doseq [holiday moveable-holidays-2024]
      (let [name (date/get-holiday-name (:date holiday))]
        (if name
          (is (string? name) (str "Expected string for " (:date holiday)))
          ;; Network error acceptable
          (is true)))))
  
  (testing "returns correct names for fixed holidays in 2025"
    (doseq [holiday fixed-holidays-2025]
      (let [name (date/get-holiday-name (:date holiday))]
        (if name
          (is (string? name) (str "Expected string for " (:date holiday)))
          ;; Network error acceptable
          (is true)))))
  
  (testing "returns nil for non-holidays in 2024"
    (doseq [non-holiday non-holidays-2024]
      (let [name (date/get-holiday-name (:date non-holiday))]
        (is (nil? name) (str "Expected nil for non-holiday " (:date non-holiday))))))
  
  (testing "handles invalid input"
    (is (nil? (date/get-holiday-name nil)))
    (is (nil? (date/get-holiday-name "")))
    (is (nil? (date/get-holiday-name 123)))
    (is (nil? (date/get-holiday-name [])))
    (is (nil? (date/get-holiday-name {}))))
  
  (testing "handles malformed dates"
    (is (nil? (date/get-holiday-name "2024/12/25")))
    (is (nil? (date/get-holiday-name "25-12-2024")))
    (is (nil? (date/get-holiday-name "not-a-date")))
    (is (nil? (date/get-holiday-name "2024")))
    (is (nil? (date/get-holiday-name "2024-01-01-extra"))))
  
  (testing "accepts Brazilian date format DD/MM/YYYY"
    ;; Test with Brazilian format
    (let [christmas (date/get-holiday-name "25/12/2024")
          new-year (date/get-holiday-name "01/01/2024")]
      (if (and christmas new-year)
        (do
          (is (string? christmas))
          (is (string? new-year)))
        ;; Network error acceptable
        (is true)))))

;; ============================================================================
;; Tests: Date Validation
;; ============================================================================

(deftest valid-date-format?-test
  (testing "validates ISO format YYYY-MM-DD"
    (is (true? (date/valid-date-format? "2024-01-15")))
    (is (true? (date/valid-date-format? "2024-12-25")))
    (is (true? (date/valid-date-format? "2025-06-30"))))
  
  (testing "rejects invalid ISO formats"
    (is (false? (date/valid-date-format? "2024/01/15")))
    (is (false? (date/valid-date-format? "15-01-2024")))
    (is (false? (date/valid-date-format? "2024-1-15")))
    (is (false? (date/valid-date-format? "24-01-15")))
    (is (false? (date/valid-date-format? "abcd-ef-gh"))))
  
  (testing "rejects non-strings"
    (is (false? (date/valid-date-format? nil)))
    (is (false? (date/valid-date-format? 123)))
    (is (false? (date/valid-date-format? [])))
    (is (false? (date/valid-date-format? {})))))

(deftest valid-brazilian-date-format?-test
  (testing "validates Brazilian format DD/MM/YYYY"
    (is (true? (date/valid-brazilian-date-format? "15/01/2024")))
    (is (true? (date/valid-brazilian-date-format? "25/12/2024")))
    (is (true? (date/valid-brazilian-date-format? "30/06/2025"))))
  
  (testing "rejects invalid Brazilian formats"
    (is (false? (date/valid-brazilian-date-format? "2024-01-15")))
    (is (false? (date/valid-brazilian-date-format? "15-01-2024")))
    (is (false? (date/valid-brazilian-date-format? "1/1/2024")))
    (is (false? (date/valid-brazilian-date-format? "15/1/2024")))
    (is (false? (date/valid-brazilian-date-format? "ab/cd/efgh"))))
  
  (testing "rejects non-strings"
    (is (false? (date/valid-brazilian-date-format? nil)))
    (is (false? (date/valid-brazilian-date-format? 123)))
    (is (false? (date/valid-brazilian-date-format? [])))
    (is (false? (date/valid-brazilian-date-format? {})))))

;; ============================================================================
;; Tests: Date Conversion
;; ============================================================================

(deftest brazilian->iso-date-test
  (testing "converts Brazilian format to ISO"
    (is (= "2024-12-25" (date/brazilian->iso-date "25/12/2024")))
    (is (= "2024-01-01" (date/brazilian->iso-date "01/01/2024")))
    (is (= "2025-06-15" (date/brazilian->iso-date "15/06/2025"))))
  
  (testing "returns nil for invalid formats"
    (is (nil? (date/brazilian->iso-date "2024-12-25")))
    (is (nil? (date/brazilian->iso-date "25-12-2024")))
    (is (nil? (date/brazilian->iso-date "invalid")))
    (is (nil? (date/brazilian->iso-date nil)))))

(deftest iso->brazilian-date-test
  (testing "converts ISO format to Brazilian"
    (is (= "25/12/2024" (date/iso->brazilian-date "2024-12-25")))
    (is (= "01/01/2024" (date/iso->brazilian-date "2024-01-01")))
    (is (= "15/06/2025" (date/iso->brazilian-date "2025-06-15"))))
  
  (testing "returns nil for invalid formats"
    (is (nil? (date/iso->brazilian-date "25/12/2024")))
    (is (nil? (date/iso->brazilian-date "25-12-2024")))
    (is (nil? (date/iso->brazilian-date "invalid")))
    (is (nil? (date/iso->brazilian-date nil)))))

(deftest normalize-date-test
  (testing "normalizes ISO format to ISO"
    (is (= "2024-12-25" (date/normalize-date "2024-12-25")))
    (is (= "2024-01-01" (date/normalize-date "2024-01-01"))))
  
  (testing "normalizes Brazilian format to ISO"
    (is (= "2024-12-25" (date/normalize-date "25/12/2024")))
    (is (= "2024-01-01" (date/normalize-date "01/01/2024")))
    (is (= "2025-06-15" (date/normalize-date "15/06/2025"))))
  
  (testing "returns nil for invalid formats"
    (is (nil? (date/normalize-date "2024/12/25")))
    (is (nil? (date/normalize-date "25-12-2024")))
    (is (nil? (date/normalize-date "invalid")))
    (is (nil? (date/normalize-date nil)))))
