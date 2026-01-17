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
;; Regex Patterns for License Plate Formats
;; ============================================================================

(def ^:const mercosul-pattern
  "Mercosul format: 3 letters + 1 digit + 1 letter + 2 digits (e.g., ABC1D23)"
  #"^[A-Z]{3}[0-9]{1}[A-Z]{1}[0-9]{2}$")

(def ^:const traditional-pattern
  "Traditional format: 3 letters + 4 digits (e.g., ABC1234)"
  #"^[A-Z]{3}[0-9]{4}$")

(def ^:const hyphen-separator
  "Pattern to remove hyphens from license plates"
  #"-")

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
    (let [normalized (str/upper-case (str/replace plate hyphen-separator ""))
          is-mercosul? (re-matches mercosul-pattern normalized)
          is-traditional? (re-matches traditional-pattern normalized)]
      (cond
        is-mercosul? "LLLNLNN"
        is-traditional? "LLLNNNN"
        :else nil))))

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
    (let [normalized (str/upper-case (str/replace plate hyphen-separator ""))
          format-type (get-format normalized)]
      (when (= format-type "LLLNNNN")
        (let [plate-letters (subs normalized 0 3)
              plate-digits (subs normalized 3 7)

              ;; Parse the digits for conversion
              first-digit (str (first plate-digits))
              second-digit-char (second plate-digits)
              second-digit-as-int (helpers/char->digit second-digit-char)
              remaining-two-digits (subs plate-digits 2 4)
              
              ;; Convert the second digit to a letter
              ascii-code-for-a 65
              letter-code (+ ascii-code-for-a second-digit-as-int)
              converted-letter (char letter-code)]
          (str plate-letters first-digit converted-letter remaining-two-digits))))))

