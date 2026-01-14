(ns brazilian-utils.license-plate
  "Brazilian license plate validation utilities.
   
   Convenient namespace that reexports all license plate validation functions.
   Supports both traditional and Mercosul license plate formats.
   
   Functions:
     - is-valid?: Validates any license plate
     - get-format: Returns format type (LLLNNNN or LLLNLNN)
     - convert-to-mercosul: Converts traditional to Mercosul format
   
   Examples:
     (require '[brazilian-utils.license-plate :as plate])
     (plate/is-valid? \"ABC-1234\") ;; true
     (plate/get-format \"ABC1234\") ;; \"LLLNNNN\"
     (plate/convert-to-mercosul \"ABC1234\") ;; \"ABC1B34\""
  (:require [brazilian-utils.license-plate.core :as core]))

(def is-valid? core/is-valid?)
(def get-format core/get-format)
(def convert-to-mercosul core/convert-to-mercosul)
