(ns brazilian-utils.pis.validation
  (:require [malli.core :as m]))

(def PISFormatted
  "Schema for formatted PIS: XXX.XXXX.XXX-X"
  [:re #"^\d{3}\.\d{4}\.\d{3}-\d{1}$"])

(defn is-valid-format?
  "Checks if PIS matches expected format (with or without formatting)."
  [pis]
  (and (string? pis)
       (m/validate [:or PISFormatted [:re "^\\d{11}$"]] pis)))
