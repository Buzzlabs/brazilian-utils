(ns brazilian-utils.date.internal
  "Internal helper functions for date operations."
  (:require [brazilian-utils.date.validation :as validation]
            [brazilian-utils.helpers :as helpers]))

(def ^:private fixed-national-holidays
  [{:month 1 :day 1 :name "Confraternização mundial" :type "fixed"}
   {:month 4 :day 21 :name "Tiradentes" :type "fixed"}
   {:month 5 :day 1 :name "Dia do trabalho" :type "fixed"}
   {:month 9 :day 7 :name "Independência do Brasil" :type "fixed"}
   {:month 10 :day 12 :name "Nossa Senhora Aparecida" :type "fixed"}
   {:month 11 :day 2 :name "Finados" :type "fixed"}
   {:month 11 :day 15 :name "Proclamação da República" :type "fixed"}
   {:month 11 :day 20 :name "Dia da consciência negra" :type "fixed"}
   {:month 12 :day 25 :name "Natal" :type "fixed"}])

(defn- pad2 [n]
  (if (< n 10) (str "0" n) (str n)))

(defn- iso-date
  [year month day]
  (str year "-" (pad2 month) "-" (pad2 day)))

(defn- easter-month-day
  "Computes Gregorian Easter date for a given year.
   Returns [month day]."
  [year]
  (let [a (mod year 19)
        b (quot year 100)
        c (mod year 100)
        d (quot b 4)
        e (mod b 4)
        f (quot (+ b 8) 25)
        g (quot (+ (- b f) 1) 3)
        h (mod (+ (* 19 a) b (- d) (- g) 15) 30)
        i (quot c 4)
        k (mod c 4)
        l (mod (+ 32 (* 2 e) (* 2 i) (- h) (- k)) 7)
        m (quot (+ a (* 11 h) (* 22 l)) 451)
        n (+ h l (- (* 7 m)) 114)
        month (quot n 31)
        day (inc (mod n 31))]
    [month day]))

(defn- add-days-iso
  "Adds days to an ISO date string (YYYY-MM-DD), returns ISO date string."
  [iso days]
  #?(:clj
     (let [d (java.time.LocalDate/parse iso)]
       (.toString (.plusDays d days)))
     :cljs
     (let [[y m d] (map js/parseInt (.split iso "-"))
           dt (js/Date. y (dec m) d)]
       (.setDate dt (+ (.getDate dt) days))
       (iso-date (.getFullYear dt) (inc (.getMonth dt)) (.getDate dt)))))

(defn national-holidays-for-year
  "Returns a vector of Brazilian national holidays for a given year.
   Includes fixed and Easter-dependent moveable holidays."
  [year]
  (let [year-int (helpers/parse-int (str year))]
    (if-not year-int
      []
      (let [[easter-month easter-day] (easter-month-day year-int)
            easter-iso (iso-date year-int easter-month easter-day)
            moveable [{:date (add-days-iso easter-iso -47) :name "Carnaval" :type "moveable"}
                      {:date (add-days-iso easter-iso -2) :name "Sexta-feira Santa" :type "moveable"}
                      {:date (add-days-iso easter-iso 60) :name "Corpus Christi" :type "moveable"}]
            fixed (map (fn [{:keys [month day name type]}]
                         {:date (iso-date year-int month day)
                          :name name
                          :type type})
                       fixed-national-holidays)]
        (vec (sort-by :date (concat fixed moveable)))))))

(defn national-holidays-response
  "Returns a map in the same shape as Brasil API responses used by this library."
  [year]
  {:status 200
   :body (national-holidays-for-year year)})

(defn extract-year
  "Safely extracts year from a date string.
  Accepts both ISO (YYYY-MM-DD) and Brazilian (DD/MM/YYYY) formats.
  
  Args:
    date (string): Date in either format
    
  Returns:
    Year as string, or nil if invalid
    
  Examples:
    (extract-year \"2024-01-15\") ;; \"2024\"
    (extract-year \"15/01/2024\") ;; \"2024\"
    (extract-year \"invalid\") ;; nil"
  [date]
  (helpers/safe-call
   #(when-let [normalized (validation/normalize-date date)]
      (when (and (string? normalized) (>= (count normalized) 4))
        (subs normalized 0 4)))
   nil))

(defn validate-and-extract-date-info
  "Validates and extracts date information.
  
  Pure function that normalizes date and extracts year.
  
  Args:
    date - Date string in any supported format
    
  Returns:
    Vector [normalized-date year] if valid, nil otherwise"
  [date]
  (let [normalized-date (validation/normalize-date date)
        year (extract-year date)]
    (when (and normalized-date year)
      [normalized-date year])))

(defn build-holiday-dates-set
  "Creates a set of holiday dates for quick lookup.
  
  Pure function that transforms holiday list into a set.
  
  Args:
    holidays - List of holiday maps with :date key
    
  Returns:
    Set of date strings"
  [holidays]
  (set (map :date holidays)))

(defn build-holiday-name-map
  "Creates a map from date to holiday name.
  
  Pure function that transforms holiday list into a map.
  
  Args:
    holidays - List of holiday maps with :date and :name keys
    
  Returns:
    Map of date -> name"
  [holidays]
  (reduce (fn [acc h]
            (assoc acc (:date h) (:name h)))
          {}
          holidays))
