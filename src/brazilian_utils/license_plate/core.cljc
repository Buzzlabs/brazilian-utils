(ns brazilian-utils.license-plate.core
  "Brazilian license plate validation and formatting utilities.
   
   Supports both traditional (ABCD1234) and Mercosul (ABC1D23) formats.
   Public API:
     - is-valid?: validate Mercosul or traditional plates
     - get-format: detect format as \"LLLNNNN\" or \"LLLNLNN\"
     - convert-to-mercosul: convert traditional plates to Mercosul style"
  (:require [brazilian-utils.license-plate.validation :as validation]
            [brazilian-utils.helpers :as helpers]
            [clojure.string :as str]))

;; ============================================================================
;; Public API
;; ============================================================================

(defn is-valid?
  "Validates if a string is a valid Brazilian license plate.
   
   Accepts both:
   - Mercosul format: 3 letters + 1 digit + 1 letter + 2 digits (e.g., ABC1D23)
   - Traditional format: 3 letters + 4 digits (e.g., ABC-1234 or ABC1234)
   
   Args:
     value - String to validate
   
   Returns:
     true if valid license plate, false otherwise
    
   Examples:
     (is-valid? \"ABC1D23\")  ;; true (Mercosul)
     (is-valid? \"ABC-1234\") ;; true (traditional)
     (is-valid? \"ABC1234\")  ;; false (ambiguous format)
     (is-valid? nil)        ;; false
     (is-valid? \"invalid\") ;; false"
  [value]
  (validation/validate-license-plate value))

(defn get-format
  "Returns the format type of a license plate string.
   
   Input: string representing a license plate (with or without hyphen, any case).
   Output:
     - \"LLLNNNN\" for traditional format (3 letters + 4 digits)
     - \"LLLNLNN\" for Mercosul format (3 letters + 1 digit + 1 letter + 2 digits)
     - nil when the plate is invalid
   
   Examples:
     (get-format \"ABC1234\")  ;; => \"LLLNNNN\"
     (get-format \"ABC1D23\")  ;; => \"LLLNLNN\"
     (get-format \"ABC-1234\") ;; => \"LLLNNNN\"
     (get-format \"ABCD123\")  ;; => nil
     (get-format \"abc1d23\")  ;; => \"LLLNLNN\""
  [plate]
  (when (and (string? plate) (not (empty? plate)))
    (let [cleaned (str/upper-case (str/replace plate #"-" ""))]
      (cond
        ;; Check Mercosul format: 3 letters + 1 digit + 1 letter + 2 digits
        (re-matches #"^[A-Z]{3}[0-9]{1}[A-Z]{1}[0-9]{2}$" cleaned)
        "LLLNLNN"
        
        ;; Check traditional format: 3 letters + 4 digits
        (re-matches #"^[A-Z]{3}[0-9]{4}$" cleaned)
        "LLLNNNN"
        
        :else
        nil))))

(defn convert-to-mercosul
  "Converts a traditional Brazilian license plate to Mercosul format.
   
   The conversion works by taking a traditional plate in the format LLLNNNN
   (3 letters + 4 digits) and converting it to LLLNLNN (3 letters + 1 digit +
   1 letter + 2 digits). The conversion rule transforms the 4 digits into
   1 digit + 1 letter + 2 digits by using the second digit to generate a letter.
   
   Input: string representing a traditional license plate (with or without hyphen).
   Output: string in Mercosul format when the plate is valid and traditional;
           nil when invalid or already Mercosul.
     
   Examples:
     (convert-to-mercosul \"ABC1234\")  ;; => \"ABC1B34\"
     (convert-to-mercosul \"abc1234\")  ;; => \"ABC1B34\"
     (convert-to-mercosul \"ABC-1234\") ;; => \"ABC1B34\"
     (convert-to-mercosul \"ABC1D23\")  ;; => nil (already Mercosul)
     (convert-to-mercosul \"invalid\")  ;; => nil"
  [plate]
  (when (and (string? plate) (not (empty? plate)))
    (let [cleaned (str/upper-case (str/replace plate #"-" ""))]
      ;; Only convert if it's in traditional format
      (when (= (get-format cleaned) "LLLNNNN")
        (let [letters (subs cleaned 0 3)
              digits (subs cleaned 3 7)
              first-digit (str (first digits))
              ;; Get second digit as a character, convert to int, then to letter
              second-digit-char (second digits)
              second-digit-int (helpers/char->digit second-digit-char)
              second-digit-code (+ 65 second-digit-int)
              converted-letter (char second-digit-code)
              remaining-digits (subs digits 2 4)]
          (str letters first-digit converted-letter remaining-digits))))))

