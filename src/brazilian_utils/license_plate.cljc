(ns brazilian-utils.license-plate
  "Brazilian license plate validation utilities.
   
   Convenient namespace that reexports all license plate validation functions.
   Supports both traditional and Mercosul license plate formats.
   
   Functions:
     - is-valid?: Validates any license plate
   
   Examples:
     (require '[brazilian-utils.license-plate :as plate])
     (plate/is-valid? \"ABC-1234\") ;; true"
  (:require [brazilian-utils.license-plate.core :as core]))

(def is-valid? core/is-valid?)