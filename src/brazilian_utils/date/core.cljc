(ns brazilian-utils.date.core
  "Utilities for working with Brazilian dates and holidays.
  
  Provides functions to:
    - Retrieve national holidays
    - Check if a date is a holiday
    - Get holiday names
    - Work with Brazilian calendar information"
  (:require [brazilian-utils.helpers :as helpers]
            [brazilian-utils.date.internal :as internal]
            [brazilian-utils.date.validation :as validation]))

(def ^:private brasilia-api-base-url "https://brasilapi.com.br/api/feriados/v1")

(defn get-holidays
  "Retrieves all national holidays for a given year from Brasil API.
  
  Args:
    year (int or string): The year to fetch holidays for
    
  Returns:
    A map with the response. On success:
      {:status 200, :body [{:date \"YYYY-MM-DD\", :name \"Holiday Name\"}]}
    On error:
      {:error \"error message\"}
      
  Examples:
    (get-holidays 2024)
    (get-holidays \"2024\")"
  [year]
  (let [year-str (str year)
        url (str brasilia-api-base-url "/" year-str)]
    (helpers/http-get url)))

(defn- fetch-holidays-safe
  "Safely fetches holidays for a year, returning nil on error.
  
  Args:
    year - The year to fetch holidays for
    
  Returns:
    Holiday list if successful, nil if error"
  [year]
  (let [response (get-holidays year)]
    (when-not (contains? response :error)
      (get-in response [:body]))))

(defn is-holiday?
  "Checks if a given date is a national holiday.
  
  Args:
    date (string): Date in ISO format (YYYY-MM-DD) or Brazilian format (DD/MM/YYYY)
    
  Returns:
    true if the date is a national holiday, false otherwise
    Returns false on invalid format or network errors
    
  Examples:
    (is-holiday? \"2024-12-25\") ;; true (Christmas)
    (is-holiday? \"25/12/2024\") ;; true (Christmas - Brazilian format)
    (is-holiday? \"01/01/2024\") ;; true (New Year - Brazilian format)
    (is-holiday? \"2024-01-15\") ;; false
    (is-holiday? \"invalid\") ;; false
    (is-holiday? nil) ;; false"
  [date]
  (helpers/safe-call
    #(let [[normalized-date year] (internal/validate-and-extract-date-info date)]
       (if-not normalized-date
         false
         (let [holidays (fetch-holidays-safe year)]
           (if-not holidays
             false
             (let [holiday-dates (internal/build-holiday-dates-set holidays)]
               (contains? holiday-dates normalized-date))))))
    false))

(defn get-holiday-name
  "Gets the name of a holiday for a given date.
  
  Args:
    date (string): Date in ISO format (YYYY-MM-DD) or Brazilian format (DD/MM/YYYY)
    
  Returns:
    The name of the holiday as a string, or nil if not a holiday or invalid format
    
  Examples:
    (get-holiday-name \"2024-12-25\") ;; \"Natal\"
    (get-holiday-name \"25/12/2024\") ;; \"Natal\" (Brazilian format)
    (get-holiday-name \"2024-01-15\") ;; nil
    (get-holiday-name \"invalid\") ;; nil"
  [date]
  (helpers/safe-call
    #(let [[normalized-date year] (internal/validate-and-extract-date-info date)]
       (if-not normalized-date
         nil
         (let [holidays (fetch-holidays-safe year)]
           (if-not holidays
             nil
             (let [holiday-name-map (internal/build-holiday-name-map holidays)]
               (get holiday-name-map normalized-date))))))
    nil))

;; Date validation and conversion functions
(def valid-date-format? validation/valid-date-format?)
(def valid-brazilian-date-format? validation/valid-brazilian-date-format?)
(def normalize-date validation/normalize-date)
(def brazilian->iso-date validation/brazilian->iso-date)
(def iso->brazilian-date validation/iso->brazilian-date)
