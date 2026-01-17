(ns brazilian-utils.boleto.internal
  "Internal helper functions for boleto validation and processing.
   
   Pure functions for DV calculation, digit parsing, and data extraction."
  (:require [brazilian-utils.helpers :as helpers]))

;; ============================================================================
;; Constants
;; ============================================================================

(def bancario-length 47)
(def arrecadacao-length 48)
(def arrecadacao-block-size 12)
(def arrecadacao-data-size 11)

(def bancario-fields
  "Definition of partial sections for 47-digit boleto validation."
  [{:range [0 9]  :dv-pos 9}      ; Field 1: 9 digits + DV
   {:range [10 20] :dv-pos 20}    ; Field 2: 10 digits + DV  
   {:range [21 31] :dv-pos 31}])  ; Field 3: 10 digits + DV

;; ============================================================================
;; Digit Parsing
;; ============================================================================

(defn parse-digit
  "Converts a character to its numeric value (0-9)."
  [ch]
  (helpers/char->digit ch))

(defn digit-at
  "Gets digit value at position in string, or nil if out of bounds."
  [digits position]
  (when (< position (count digits))
    (parse-digit (nth digits position))))

;; ============================================================================
;; DV Calculation Algorithms
;; ============================================================================

(defn dv-mod10
  "Calcula DV usando Módulo 10 (Luhn alternado 2-1)."
  [digits]
  (let [sum (->> (reverse digits)
                 (map-indexed (fn [idx ch]
                                (let [d (parse-digit ch)
                                      res (* d (if (even? idx) 2 1))]
                                  (if (> res 9) (- res 9) res))))
                 (reduce +))
        rem (mod sum 10)]
    (if (zero? rem) 0 (- 10 rem))))

(defn dv-mod10-arrec
  "Cálculo Módulo 10 para Arrecadação (Luhn 2-1)."
  [digits]
  (let [sum (->> (reverse digits)
                 (map parse-digit)
                 (map * (cycle [2 1]))
                 (map #(if (> % 9) (- % 9) %))
                 (reduce +))
        remainder (mod sum 10)]
    (if (zero? remainder) 
      0 
      (- 10 remainder))))

(defn dv-mod11-arrec
  "Cálculo Módulo 11 para Arrecadação (Pesos 2-9)."
  [digits]
  (let [sum (->> (reverse digits)
                 (map parse-digit)
                 (map * (cycle [2 3 4 5 6 7 8 9]))
                 (reduce +))
        remainder (mod sum 11)]
    ;; Regra específica Arrecadação:
    ;; Se resto 0 ou 1 -> DV é 0
    ;; Se resto 10 -> DV é 0 (algumas implementações tratam como 0)
    (if (or (= remainder 0) (= remainder 1) (= remainder 10))
      0
      (- 11 remainder))))

(defn dv-mod11-bank
  "Calcula DV usando Módulo 11 para boletos bancários (pesos 2-9)."
  [digits]
  (let [sum (->> digits
                 reverse
                 (map parse-digit)
                 (map * (cycle (range 2 10)))
                 (reduce +))
        remainder (mod sum 11)]
    (if (#{0 1 10} remainder) 1 (- 11 remainder))))

(defn resolve-arrecadacao-dv
  "Define o algoritmo baseado no 3º dígito (Identificador de Valor)."
  [digits]
  (let [id-valor (nth digits 2)]
    (case id-valor
      (\6 \7) dv-mod10-arrec
      (\8 \9) dv-mod11-arrec
      ;; Fallback: Para dígitos 1-5, o padrão de mercado é Mod 10
      dv-mod10-arrec)))

;; ============================================================================
;; Coercion and Conversion
;; ============================================================================

(defn coerce-bancario-digits
  "Pad a 46-digit bank boleto by inserting one zero at position 38 so it matches 47 digits; otherwise return unchanged."
  [digits]
  (cond
    (and (= 46 (count digits)) (<= 38 (count digits)))
    (str (subs digits 0 38) "0" (subs digits 38))

    :else digits))

(defn linha->barcode
  "Converts a digitable line to boleto/barcode format (44 digits)."
  [digits]
  (str (subs digits 0 4) (subs digits 32 47) (subs digits 4 9) (subs digits 10 20) (subs digits 21 31)))

;; ============================================================================
;; Block Validation
;; ============================================================================

(defn arrecadacao-blocks
  "Splits arrecadação digits into blocks of 12."
  [digits]
  (partition arrecadacao-block-size digits))

(defn valid-arrecadacao-block?
  "Validates a single arrecadação block using provided DV function."
  [block dv-fn]
  (let [data (take arrecadacao-data-size block)
        actual-dv (parse-digit (last block))
        expected-dv (dv-fn (apply str data))]
    (= actual-dv expected-dv)))

(defn valid-field?
  "Validates a single bancário field against its DV."
  [{:keys [range dv-pos]} digits]
  (let [[start end] range
        field (subs digits start end)
        calculated-dv (dv-mod10 field)
        actual-dv (digit-at digits dv-pos)]
    (= actual-dv calculated-dv)))

;; ============================================================================
;; Data Extraction
;; ============================================================================

(defn extract-bank-info
  "Extracts bank code and currency from boleto digits.
   
   Args:
     digits - Boleto digit string (minimum 4 digits)
   
   Returns:
     Map with :bank-code (3 digits) and :currency (1 digit)"
  [digits]
  {:bank-code (subs digits 0 3)
   :currency  (subs digits 3 4)})

(defn extract-due-date-factor
  "Extracts due date factor from boleto digits.
   
   The factor represents days since base date (1997-10-07).
   
   Args:
     digits - Boleto digit string (minimum 37 digits)
   
   Returns:
     Integer representing days since base date"
  [digits]
  (helpers/parse-int (subs digits 33 37)))

(defn extract-value
  "Extracts boleto value in cents from boleto digits.
   
   Args:
     digits - Boleto digit string (minimum 47 digits)
   
   Returns:
     Long integer representing value in cents"
  [digits]
  (parse-long (subs digits 37 47)))

(defn calculate-due-date
  "Calculates due date from factor (days since 1997-10-07).
   
   Args:
     due-date-factor - Integer days since base date
   
   Returns:
     ISO date string (YYYY-MM-DD) or nil if factor is 0 or negative"
  [due-date-factor]
  (when (> due-date-factor 0)
    #?(:clj
       (let [base-date (java.time.LocalDate/of 1997 10 7)]
         (.format (.plusDays base-date due-date-factor)
                  java.time.format.DateTimeFormatter/ISO_LOCAL_DATE))
       :cljs
       (let [base-date (js/Date. 1997 9 7)] ; months are 0-indexed in JS
         (.setDate base-date (+ (.getDate base-date) due-date-factor))
         (.toISOString base-date)))))
