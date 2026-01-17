(ns brazilian-utils.renavam.internal
  "Internal helpers for RENAVAM validation."
  (:require [brazilian-utils.helpers :as helpers]))

(def ^:const renavam-length 11)
(def ^:const old-renavam-length 9)
(def ^:const base-length 10)
(def ^:const weights [3 2 9 8 7 6 5 4 3 2])

(defn normalize-length
  "Pads old 9-digit RENAVAMs with leading zeros to 11 digits.
  Returns nil when length is not 9 or 11."
  [digits]
  (let [len (count digits)]
    (cond
      (= len renavam-length) digits
      (= len old-renavam-length) (str (apply str (repeat (- renavam-length len) "0")) digits)
      :else nil)))

(defn calculate-dv
  "Calculates RENAVAM check digit from the first 10 digits."
  [base10]
  (when (= base-length (count base10))
    (let [sum (helpers/weighted-sum base10 weights)
          remainder (mod sum 11)]
      (if (or (zero? remainder) (= remainder 1))
        0
        (- 11 remainder)))))

(defn valid-check-digit?
  "Checks if the final digit matches the calculated check digit."
  [normalized]
  (when (= renavam-length (count normalized))
    (let [base (subs normalized 0 base-length)
          expected (calculate-dv base)
          actual (helpers/char->digit (get normalized 10))]
      (and expected (= expected actual)))))
