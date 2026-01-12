(ns brazilian-utils.capitalize.core
  "Capitalize strings with support for prepositions and acronyms.
  
  Functions for capitalizing strings in a way that respects Brazilian Portuguese
  conventions, handling prepositions, acronyms, and custom word rules.
  
  Examples:
    (capitalize \"esponja de aço\") ;; => \"Esponja de Aço\"
    (capitalize \"josé ama maria\" {:lower-case-words [\"ama\"]}) ;; => \"José ama Maria\""
  (:require [clojure.string :as str]))

(def ^:const acronyms
  "Common Brazilian acronyms that should always be uppercase."
  ["cia" "cnpj" "cpf" "ltda" "me" "rg"])

(def ^:const prepositions
  "Common Portuguese prepositions and conjunctions that should be lowercase."
  ["a" "com" "da" "das" "de" "do" "dos" "e" "em" "na" "nas" "no" "nos" "o" "por" "sem"])

(defn- capitalize-word
  "Capitalizes a single word: uppercase first letter, rest lowercase."
  [word]
  (str/capitalize (str/lower-case word)))

(defn capitalize
  "Capitalizes a string according to capitalization rules.
  
  Rules:
  - Words in lower-case-words (default: PREPOSITIONS) are kept lowercase, except the first word
  - Words in upper-case-words (default: ACRONYMS) are converted to uppercase
  - All other words have first letter capitalized, rest lowercase
  - Multiple spaces are normalized to single spaces
  - Empty strings are returned as-is
  
  Args:
    text - The string to capitalize
    options - Optional map with:
              :lower-case-words - Vector of words to keep lowercase (default: PREPOSITIONS)
              :upper-case-words - Vector of words to keep uppercase (default: ACRONYMS)
  
  Returns:
    The capitalized string
    
  Examples:
    (capitalize \"esponja de aço\") ;; => \"Esponja de Aço\"
    (capitalize \"josé ama maria\" {:lower-case-words [\"ama\"]}) ;; => \"José ama Maria\"
    (capitalize \"doc da empresa ab\" {:upper-case-words [\"DOC\" \"AB\"]}) ;; => \"DOC da Empresa AB\""
  ([text]
   (capitalize text {}))
  ([text {:keys [lower-case-words upper-case-words] :or {lower-case-words prepositions upper-case-words acronyms}}]
   (let [normalized-text (-> text
                             str/trim
                             (str/replace #"\s+" " "))]
     (if (empty? normalized-text)
       ""
       (let [words (str/split normalized-text #" ")
             lower-case-set (->> lower-case-words (map str/lower-case) set)
             upper-case-set (->> upper-case-words (map str/lower-case) set)
             
             capitalized-words (map-indexed
                                (fn [idx word]
                                  (let [lower-word (str/lower-case word)]
                                    (cond
                                      ;; Check upper-case-words list (before checking first word)
                                      (contains? upper-case-set lower-word) (str/upper-case lower-word)
                                      
                                      ;; Check lower-case-words list (except first word)
                                      (and (not (zero? idx)) (contains? lower-case-set lower-word)) lower-word
                                      
                                      ;; First word or default: capitalize
                                      :else (capitalize-word word))))
                                words)]
         (str/join " " capitalized-words))))))
