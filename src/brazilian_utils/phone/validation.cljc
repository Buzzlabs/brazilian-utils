(ns brazilian-utils.phone.validation
  (:require #?(:clj  [clojure.string :as str]
               :cljs [clojure.string :as str])
            [brazilian-utils.helpers :as helpers]
            [malli.core :as m]
            [malli.error :as me]))

;; Valid Brazilian area codes (DDDs)
(def valid-area-codes
  #{11 12 13 14 15 16 17 18 19 ; São Paulo
    21 22 24 ; Rio de Janeiro
    27 28 ; Espírito Santo
    31 32 33 34 35 37 38 ; Minas Gerais
    41 42 43 44 45 46 ; Paraná
    47 48 49 ; Santa Catarina
    51 53 54 55 ; Rio Grande do Sul
    61 ; Distrito Federal
    62 64 ; Goiás
    63 ; Tocantins
    65 66 ; Mato Grosso
    67 ; Mato Grosso do Sul
    68 ; Acre
    69 ; Rondônia
    71 73 74 75 77 ; Bahia
    79 ; Sergipe
    81 87 ; Pernambuco
    82 ; Alagoas
    83 ; Paraíba
    84 ; Rio Grande do Norte
    85 88 ; Ceará
    86 89 ; Piauí
    91 93 94 ; Pará
    92 97 ; Amazonas
    95 ; Roraima
    96 ; Amapá
    98 99}) ; Maranhão

(def ^:const phone-min-length 10)
(def ^:const phone-max-length 11)
(def mobile-valid-first-numbers #{6 7 8 9})
(def landline-valid-first-numbers #{2 3 4 5})

(defn- parse-phone-digits
  "Parse phone string and return digits"
  [phone]
  (when (and phone (string? phone))
    (helpers/only-numbers phone)))

(defn- valid-ddd?
  "Check if DDD (area code) is valid"
  [phone-digits]
  (when (and (string? phone-digits) (>= (count phone-digits) 2))
    (let [ddd #?(:clj (parse-long (subs phone-digits 0 2))
                  :cljs (js/parseInt (subs phone-digits 0 2) 10))]
      (contains? valid-area-codes ddd))))

(defn- valid-mobile-length?
  "Check if phone has valid mobile length (11 digits)"
  [phone-digits]
  (and (string? phone-digits) 
       (= (count phone-digits) phone-max-length)))

(defn- valid-landline-length?
  "Check if phone has valid landline length (10 digits)"
  [phone-digits]
  (and (string? phone-digits) 
       (= (count phone-digits) phone-min-length)))

(defn- valid-length?
  "Check if phone has valid length (10 or 11 digits)"
  [phone-digits]
  (or (valid-mobile-length? phone-digits)
      (valid-landline-length? phone-digits)))

(defn- valid-mobile-first-number?
  "Check if first number after DDD is valid for mobile (6,7,8,9)"
  [phone-digits]
  (when (and (string? phone-digits) (>= (count phone-digits) 3))
    (let [first-num #?(:clj (parse-long (str (nth phone-digits 2)))
                       :cljs (js/parseInt (str (nth phone-digits 2)) 10))]
      (contains? mobile-valid-first-numbers first-num))))

(defn- valid-landline-first-number?
  "Check if first number after DDD is valid for landline (2,3,4,5)"
  [phone-digits]
  (when (and (string? phone-digits) (>= (count phone-digits) 3))
    (let [first-num #?(:clj (parse-long (str (nth phone-digits 2)))
                       :cljs (js/parseInt (str (nth phone-digits 2)) 10))]
      (contains? landline-valid-first-numbers first-num))))

(defn- valid-first-number?
  "Check if first number after DDD is valid based on phone length"
  [phone-digits]
  (cond
    (valid-mobile-length? phone-digits) (valid-mobile-first-number? phone-digits)
    (valid-landline-length? phone-digits) (valid-landline-first-number? phone-digits)
    :else false))

(def Phone
  "Schema for Brazilian phone validation"
  [:and
   :string
   [:fn
    {:error/message "Phone number cannot be blank"}
    (fn [s] (not (str/blank? s)))]
   [:fn
    {:error/message "Invalid phone format - must contain only digits and formatting characters"}
    (fn [s] (some? (parse-phone-digits s)))]
   [:fn
    {:error/message (str "Phone number must have between " phone-min-length " and " phone-max-length " digits")}
    (fn [s] 
      (when-let [digits (parse-phone-digits s)]
        (valid-length? digits)))]
   [:fn
    {:error/message "Invalid area code (DDD)"}
    (fn [s]
      (when-let [digits (parse-phone-digits s)]
        (valid-ddd? digits)))]
   [:fn
    {:error/message "Invalid first digit after area code"}
    (fn [s]
      (when-let [digits (parse-phone-digits s)]
        (valid-first-number? digits)))]])

(def MobilePhone
  "Schema for Brazilian mobile phone validation"
  [:and
   :string
   [:fn
    {:error/message "Mobile phone number cannot be blank"}
    (fn [s] (not (str/blank? s)))]
   [:fn
    {:error/message "Invalid mobile phone format"}
    (fn [s] (some? (parse-phone-digits s)))]
   [:fn
    {:error/message (str "Mobile phone number must have exactly " phone-max-length " digits")}
    (fn [s] 
      (when-let [digits (parse-phone-digits s)]
        (valid-mobile-length? digits)))]
   [:fn
    {:error/message "Invalid area code (DDD) for mobile phone"}
    (fn [s]
      (when-let [digits (parse-phone-digits s)]
        (valid-ddd? digits)))]
   [:fn
    {:error/message "Mobile phone first digit must be 6, 7, 8, or 9"}
    (fn [s]
      (when-let [digits (parse-phone-digits s)]
        (valid-mobile-first-number? digits)))]])

(def LandlinePhone
  "Schema for Brazilian landline phone validation"
  [:and
   :string
   [:fn
    {:error/message "Landline phone number cannot be blank"}
    (fn [s] (not (str/blank? s)))]
   [:fn
    {:error/message "Invalid landline phone format"}
    (fn [s] (some? (parse-phone-digits s)))]
   [:fn
    {:error/message (str "Landline phone number must have exactly " phone-min-length " digits")}
    (fn [s] 
      (when-let [digits (parse-phone-digits s)]
        (valid-landline-length? digits)))]
   [:fn
    {:error/message "Invalid area code (DDD) for landline phone"}
    (fn [s]
      (when-let [digits (parse-phone-digits s)]
        (valid-ddd? digits)))]
   [:fn
    {:error/message "Landline phone first digit must be 2, 3, 4, or 5"}
    (fn [s]
      (when-let [digits (parse-phone-digits s)]
        (valid-landline-first-number? digits)))]])

(defn is-valid-phone?
  "Validates a phone number (mobile or landline)."
  [phone]
  (m/validate Phone phone))

(defn is-valid-mobile-phone?
  "Validates a mobile phone number."
  [phone]
  (m/validate MobilePhone phone))

(defn is-valid-landline-phone?
  "Validates a landline phone number."
  [phone]
  (m/validate LandlinePhone phone))

(defn explain-phone
  "Returns validation errors for a phone number."
  [phone]
  (if (is-valid-phone? phone)
    []
    (me/humanize (m/explain Phone phone))))

(defn explain-mobile-phone
  "Returns validation errors for a mobile phone number."
  [phone]
  (if (is-valid-mobile-phone? phone)
    []
    (me/humanize (m/explain MobilePhone phone))))

(defn explain-landline-phone
  "Returns validation errors for a landline phone number."
  [phone]
  (if (is-valid-landline-phone? phone)
    []
    (me/humanize (m/explain LandlinePhone phone))))