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

(defn- process-word
  "Processes a single word based on capitalization rules.
  
  Args:
    word - The word to process
    idx - Index of the word (0-based)
    lower-case-set - Set of words that should be lowercase
    upper-case-set - Set of words that should be uppercase
    
  Returns:
    The processed word according to the rules"
  [word idx lower-case-set upper-case-set]
  (let [lower-word (str/lower-case word)
        is-acronym? (contains? upper-case-set lower-word)
        is-preposition? (contains? lower-case-set lower-word)
        is-first-word? (zero? idx)]
    (cond
      is-acronym? (str/upper-case lower-word)
      (and (not is-first-word?) is-preposition?) lower-word
      :else (capitalize-word word))))

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
  ([text {:keys [lower-case-words upper-case-words] 
          :or {lower-case-words prepositions 
               upper-case-words acronyms}}]
   (let [normalized-text (-> text str/trim (str/replace #"\s+" " "))]
     (if (empty? normalized-text)
       ""
       (let [words (str/split normalized-text #" ")
             lower-case-set (set (map str/lower-case lower-case-words))
             upper-case-set (set (map str/lower-case upper-case-words))
             capitalized-words (map-indexed 
                                 #(process-word %2 %1 lower-case-set upper-case-set)
                                 words)]
         (str/join " " capitalized-words))))))
