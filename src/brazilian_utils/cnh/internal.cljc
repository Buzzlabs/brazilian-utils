(ns brazilian-utils.cnh.internal
  "Internal utilities for CNH (Carteira Nacional de Habilitação) validation."
  (:require [brazilian-utils.helpers :as helpers]))

(def ^:const cnh-length 11)
(def ^:const base-length 9)

(defn- weighted-sum-desc
  "Weighted sum using descending weights starting from `start-weight`.
  Applies weights left-to-right as start, start-1, ..."
  [digits start-weight]
  (helpers/weighted-sum digits start-weight))

(defn calculate-dv1
  "Calculates DV1 for the first 9 digits of a CNH.
  Returns map {:dv dv1 :penalize? true|false} where penalize? is true when
  the raw remainder was 10 (DV1 becomes 0)."
  [base9]
  (when (= base-length (count base9))
    (let [sum (weighted-sum-desc base9 10)
          remainder (mod sum 11)
          penalize? (= remainder 10)
          dv (if penalize? 0 remainder)]
      {:dv dv :penalize? penalize?})))

(defn calculate-dv2
  "Calculates DV2 for a CNH given base digits, DV1, and penalty flag."
  [base9 dv1 penalize?]
  (when (= base-length (count base9))
    (let [digits (str base9 dv1)
          sum (weighted-sum-desc digits 12)
          sum* (if penalize? (- sum 2) sum)
          remainder (mod sum* 11)]
      (if (= remainder 10) 0 remainder))))

(defn valid-check-digits?
  "Validates CNH check digits for a cleaned 11-digit string."
  [cnh]
  (when (= cnh-length (count cnh))
    (let [base (subs cnh 0 base-length)
          {:keys [dv penalize?]} (calculate-dv1 base)
          expected-dv1 dv
          expected-dv2 (calculate-dv2 base expected-dv1 penalize?)
          actual-dv1 (helpers/char->digit (get cnh 9))
          actual-dv2 (helpers/char->digit (get cnh 10))]
      (and expected-dv1 expected-dv2
           (= expected-dv1 actual-dv1)
           (= expected-dv2 actual-dv2)))))
