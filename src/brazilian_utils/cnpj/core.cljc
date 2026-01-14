(ns brazilian-utils.cnpj.core
  (:require [brazilian-utils.cnpj.internal :as i]
            [brazilian-utils.cnpj.format :as fmt]
            [brazilian-utils.cnpj.validation :as validation]
            [brazilian-utils.helpers :as helpers]))

(defn remove-symbols
  "Normalizes a CNPJ by stripping non-alphanumerics and uppercasing.

   Args:
     cnpj - String (formatted or not); nil allowed

   Returns:
     Up to 14 uppercase A-Z/0-9 characters; nil becomes empty string

  Examples:
  (remove-symbols \"12.345.678/0001-95\") ;; => \"12345678000195\"
  (remove-symbols \"ab1c2d3e4f5g19\")    ;; => \"AB1C2D3E4F5G19\"
  (remove-symbols \"\")                  ;; => \"\""
  [cnpj]
  (i/clean-alfanumeric (str cnpj)))

(defn format-cnpj
  "Formats a CNPJ with standard Brazilian mask (XX.XXX.XXX/XXXX-XX).
  Supports numeric, alphanumeric, partial, and optional zero-padding.

   Args:
     cnpj - String or number to format
     opts - Optional map for padding rules

   Returns:
     CNPJ string formatted with mask"
  ([cnpj] (fmt/format-cnpj cnpj))
  ([cnpj opts] (fmt/format-cnpj cnpj opts)))

(defn generate
  "Generates a valid numeric CNPJ (14 digits, unformatted).

  Args: none
  Returns: 14-digit numeric string (digits only); avoids repeated-digit bases"
  []
  (let [rand-base (fn [] (apply str (repeatedly 12 #(rand-int 10))))
        base (loop [b (rand-base)]
               (if (re-matches #"^(\d)\1{11}$" b) ; avoid all repeated
                 (recur (rand-base))
                 b))
        dv1 (i/calc-check-digit* base i/first-check-digit-weights)
        dv2 (i/calc-check-digit* (str base dv1) i/second-check-digit-weights)]
    (str base dv1 dv2)))

(defn generate-alfanumeric
  "Generates a valid alphanumeric CNPJ (14 chars, unformatted).

  Args: none
  Returns: 14-character alphanumeric string; check digits remain numeric"
  []
  (let [chars "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        rand-ch (fn [] (nth chars (rand-int (count chars))))
        base (apply str (repeatedly 12 rand-ch))
        dv1 (i/calc-check-digit* base i/first-check-digit-weights)
        dv2 (i/calc-check-digit* (str base dv1) i/second-check-digit-weights)]
    (str base dv1 dv2)))

(defn is-valid?
  "Validates numeric or alphanumeric CNPJ (IN RFB nº 2.229/2024).

   Args:
     cnpj - String (formatted or not); nil returns false

   Rules: 14 chars after cleaning, not all repeated, mod-11 check digits correct,
   and if letters are present it must fit alphanumeric shape (12 A-Z/0-9 + 2 digits).

   Returns: true when valid; false otherwise"
  [cnpj]
  (if-not (string? cnpj)
    false
    (let [cleaned (i/clean-alfanumeric cnpj)
          contains-letter? (boolean (re-find #"[A-Z]" cleaned))
          length-ok? (= (count cleaned) i/cnpj-length)
          shape-ok? (and length-ok?
                         (if contains-letter?
                           (validation/is-alfanumeric? cleaned)
                           (validation/is-numeric? cleaned)))]
      (boolean
        (and shape-ok?
             (not (helpers/repeated-digits? cleaned i/cnpj-length))
             (i/valid-checksum* cleaned))))))
