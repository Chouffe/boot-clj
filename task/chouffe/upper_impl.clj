(ns chouffe.upper-impl
  (:require [clojure.java.io :as io]))

(defn compile-lc
  [lc-file uc-path]
  (->> lc-file
       slurp
       .toUpperCase
       (spit uc-path)))
