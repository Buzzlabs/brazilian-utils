(ns brazilian-utils.cnpj.format
  (:require [brazilian-utils.cnpj.internal :as i]))

(defn format-cnpj
  "Formats numeric or alphanumeric CNPJ as XX.XXX.XXX/XXXX-XX.
   Supports partials and optional zero-padding."
  ([cnpj] (format-cnpj cnpj {}))
  ([cnpj opts]
   (let [cleaned (i/clean-alfanumeric cnpj)
         contains-letter? (boolean (re-find #"[A-Z]" cleaned))]
     (if contains-letter?
       (i/format-alfanumeric cleaned opts)
       (i/format-numeric cleaned opts)))))
