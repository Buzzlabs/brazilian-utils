(ns brazilian-utils.helpers
  "Shared helper functions used across brazilian-utils modules.
  
  Provides common utility functions for string manipulation and validation
  used by other modules in the library.
  
  Functions:
    - only-numbers: Extract digits from strings
    - repeated-digits?: Check if string has all same digits
    - char->digit: Convert digit character to numeric value
    - http-get: Make HTTP GET requests"
  (:require [clojure.string :as str]
            #?(:clj [clj-http.client :as http])
            #?(:cljs [cljs-http.client :as http])))

(defn only-numbers
  "Removes all non-numeric characters from a string.
   
   Arguments:
   - value: Input to extract only numbers from
   
   Returns a string containing only digits (0-9).
   
   Example:
   (only-numbers \"123.456.789-00\") ;; \"12345678900\"
   (only-numbers \"01310-100\") ;; \"01310100\"
   (only-numbers nil) ;; \"\""
  [value]
  (-> (or value "")
      str
      (str/replace #"[^0-9]" "")))

(defn repeated-digits?
  "Checks if a string contains only repeated digits of a specified length.
   
   Arguments:
   - value: Input to check (will be normalized to remove non-digits)
   - length: Expected length of digits (optional, defaults to any length >= 1)
   
   Returns true if all digits are the same, false otherwise.
   
   Example:
   (repeated-digits? \"00000000\" 8)  ;; true  
   (repeated-digits? \"00000-000\" 8) ;; true
   (repeated-digits? \"11111111\")    ;; true (any length)
   (repeated-digits? \"01310100\")    ;; false
   (repeated-digits? \"000\" 8)       ;; false (wrong length)"
  ([text]
   (repeated-digits? text nil))
  ([text expected-length]
   (if (string? text)
     (let [clean-digits (only-numbers text)
           digit-count (count clean-digits)]
       (and (pos? digit-count)
            (or (not expected-length) (= expected-length digit-count))
            (empty? (rest (distinct clean-digits)))))
     false)))

(defn char->digit
  "Converts a character digit (0-9) to its numeric value.
   
   Arguments:
   - c: A character or single-character string
   
   Returns the numeric value (0-9) as an integer.
   
   Example:
     (char->digit \" 5 \") ;; => 5
     (char->digit \\5) ;; => 5"
  [c]
  (let [c (first (str c))
        code #?(:clj (int c)
                :cljs (.charCodeAt c 0))]
    (- code 48)))

(defn weighted-sum
  "Computes the weighted sum of digit characters.

    digits    - string/seq of characters to convert
    weights   - either a descending start weight number (e.g., 10) or a seq of weights
    char->val - optional fn to convert each char to numeric value (defaults to char->digit)

    Returns the integer sum or nil if weight count does not match digit count."
  ([digits weights]
   (weighted-sum digits weights char->digit))
  ([digits weights char->val]
   (let [ds (seq (or digits ""))
         ws (if (number? weights)
              (map #(- weights %) (range (count ds)))
              weights)]
     (when (= (count ds) (count ws))
       (reduce + (map * (map char->val ds) ws))))))

(defn check-digit
  "Computes check digit using modulo 11 with given weights.
   
   Arguments:
   - digits: String or sequence of digits
   - weights: Vector of weights (e.g., [9 8 7 6 5 4 3 2])
   - options: Optional map with:
     - :char->val (default char->digit) - function to convert char to int
     - :threshold (default 2) - remainder < threshold returns 0, else 11 - remainder
     - :stringify? (default false) - return as string instead of int
   
   Returns the digit (int or string) or nil if weight count does not match.
   
   Example:
   (check-digit \"12000000\" [9 8 7 6 5 4 3 2]) ;; => 8
   (check-digit \"11004249\" [1 3 4 5 6 7 8 10]) ;; => 0"  
  ([digits weights]
   (check-digit digits weights {}))
  ([digits weights {:keys [char->val threshold stringify?]
                    :or {char->val char->digit
                         threshold 2
                         stringify? false}}]
   (when-let [sum (weighted-sum digits weights char->val)]
     (let [remainder (mod sum 11)
           digit (if (< remainder threshold) 0 (- 11 remainder))]
       (if stringify? (str digit) digit)))))

(defn slice-safe
  "Safe substring that handles bounds correctly.
   
   Returns substring from start to end, handling cases where indices
   exceed string length. Returns nil if start is beyond string length.
   
   Arguments:
   - s: String to slice
   - start: Start index (inclusive)
   - end: End index (exclusive)
   
   Returns substring or nil if start exceeds string length.
   
   Example:
   (slice-safe \"hello\" 0 3) ;; \"hel\"
   (slice-safe \"hello\" 2 10) ;; \"llo\" (handles overflow)
   (slice-safe \"hello\" 10 15) ;; nil (start beyond length)"
  [s start end]
  (when (< start (count s))
    (subs s start (min end (count s)))))

(defn digits->ints
  "Converts a string of digit characters to a sequence of integers.
   
   Arguments:
   - s: String containing digit characters
   
   Returns a lazy sequence of integers.
   
   Example:
   (digits->ints \"12345\") ;; => (1 2 3 4 5)
   (digits->ints \"000\") ;; => (0 0 0)"
  [s]
  (map char->digit (seq s)))

(defn sum-digits
  "Sums all digit characters in a string.
   
   Arguments:
   - s: String containing digit characters
   
   Returns the sum of all digits as an integer.
   
   Example:
   (sum-digits \"123\") ;; => 6
   (sum-digits \"99\") ;; => 18"
  [s]
  (reduce + (digits->ints s)))

(defn parse-int
  "Parses a string to an integer in a cross-platform way.
   
   Arguments:
   - s: String to parse
   
   Returns the integer value or nil if parsing fails.
   
   Example:
   (parse-int \"123\") ;; => 123
   (parse-int \"42\") ;; => 42"
  [s]
  #?(:clj  (try (Integer/parseInt s) (catch Exception _ nil))
     :cljs (let [n (js/parseInt s 10)]
             (if (js/isNaN n) nil n))))

;; Note: Using clojure.core/parse-long directly instead of defining our own
;; to avoid shadowing the core function. Core's parse-long is available in Clojure 1.11+

(defn random-digits
  "Generates a string of random digits.
   
   Arguments:
   - n: Number of digits to generate
   
   Returns a string of n random digits.
   
   Example:
   (random-digits 5) ;; => \"38291\"
   (random-digits 3) ;; => \"742\""
  [n]
  (apply str (repeatedly n #(rand-int 10))))

(defn http-get
  "Makes an HTTP GET request to the given URL.
  
  Args:
    url (string): The URL to request
    
  Returns:
    A map with the response body on success, or an error map on failure.
    Success: {:status 200, :body response-data}
    Error: {:error error-message}"
  [url]
  #?(:clj
     (try
       (let [response (http/get url {:as :json})]
         {:status (:status response)
          :body (:body response)})
       (catch Exception e
         {:error (.getMessage e)}))
     :cljs
     (js/Promise.
      (fn [resolve _reject]
        (-> (http/get url {:with-credentials? false})
            (.then (fn [response]
                     (resolve {:status (:status response)
                               :body (:body response)})))
            (.catch (fn [error]
                      (resolve {:error (.-message error)}))))))))

(defn safe-call
  "Safely executes a function, returning a default value on any exception.
  
  Works cross-platform with both Clojure and ClojureScript.
  
  Args:
    f (function): Zero-argument function to execute
    default-value: Value to return if exception occurs
    
  Returns:
    Result of (f) on success, or default-value on error
    
  Examples:
    (safe-call #(get-holidays 2024) {:error \"fallback\"})
    (safe-call #(is-holiday? \"2024-01-01\") false)
    (safe-call #(parse-long \"123\") nil)"
  [f default-value]
  (try
    (f)
    (catch #?(:clj Throwable :cljs :default) _
      default-value)))

(defn validate-string-format
  "Validates if a string matches a specific pattern defined by length,
  separator positions, and digit positions.
  
  Args:
    s (string): String to validate
    length (int): Expected total length
    separator-char (char): Expected separator character (e.g., \\- or \\/)
    separator-positions (vector): Indices where separators should appear
    
  Returns:
    true if format matches, false otherwise
    
  Examples:
    (validate-string-format \"2024-01-15\" 10 \\- [4 7]) ;; true (ISO date)
    (validate-string-format \"15/01/2024\" 10 \\/ [2 5]) ;; true (Brazilian date)
    (validate-string-format \"01310-100\" 9 \\- [5]) ;; true (CEP)"
  [s length separator-char separator-positions]
  (and (string? s)
       (= (count s) length)
       (every? #(= (get s %) separator-char) separator-positions)
       (let [sep-set (set separator-positions)
             digit-positions (remove #(contains? sep-set %) (range length))]
         (every? (fn [pos] 
                   (let [c (get s pos)]
                     (and c (re-matches #"\d" (str c)))))
                 digit-positions))))

(defn split-and-rejoin
  "Splits a string by positions and rejoins with new separator.
  
  Args:
    s (string): String to process
    split-positions (vector): Indices to split at (e.g., [4 7] for YYYY-MM-DD)
    new-separator (string): New separator to use when rejoining
    
  Returns:
    Reformatted string or nil if invalid
    
  Examples:
    (split-and-rejoin \"20240115\" [4 6] \"-\") ;; \"2024-01-15\"
    (split-and-rejoin \"15012024\" [2 4] \"/\") ;; \"15/01/2024\""
  [s split-positions new-separator]
  (when (and (string? s) 
             (<= (last split-positions) (count s)))
    (let [positions (concat [0] split-positions [(count s)])
          parts (map (fn [[start end]] (subs s start end))
                     (partition 2 1 positions))]
      (str/join new-separator parts))))

(defn extract-date-parts
  "Extracts day, month, and year from a date string.
  
  Args:
    date (string): Date string
    format (keyword): Either :iso (YYYY-MM-DD) or :brazilian (DD/MM/YYYY)
    
  Returns:
    Map with :day, :month, :year keys, or nil if invalid
    
  Examples:
    (extract-date-parts \"2024-01-15\" :iso) ;; {:year \"2024\", :month \"01\", :day \"15\"}
    (extract-date-parts \"15/01/2024\" :brazilian) ;; {:day \"15\", :month \"01\", :year \"2024\"}"
  [date format]
  (when (string? date)
    (case format
      :iso (when (= (count date) 10)
             {:year (subs date 0 4)
              :month (subs date 5 7)
              :day (subs date 8 10)})
      :brazilian (when (= (count date) 10)
                   {:day (subs date 0 2)
                    :month (subs date 3 5)
                    :year (subs date 6 10)})
      nil)))

(defn mod-large-number
  "Calculates modulo for very large numbers in both Clojure and ClojureScript.
   Handles BigInt conversion for cross-platform compatibility.
   
   Args:
     num-str - Number as string (for big integer support)
     divisor - The divisor for modulo operation
   
   Returns:
     The remainder of num-str divided by divisor
   
   Examples:
     (mod-large-number \"12345678901234567890\" 97)  ;; => 1
     (mod-large-number \"98765432109876543210\" 11)  ;; => 5"
  [num-str divisor]
  #?(:clj (mod (bigint num-str) divisor)
     :cljs (js/Number (mod (js/BigInt num-str) (js/BigInt divisor)))))