(ns opdsp.shared
  (:require [clj-yaml.core :as yaml])
  (:import (java.io File)))

(def settingsPath (clojure.string/join "/" [ (java.lang.System/getenv "HOME") ".opds-p"]))

(def app-settings  (let [file (clojure.string/join "/" [settingsPath "app-settings.yaml"])]
                     (if (.exists (File. file))
                       (yaml/parse-string (slurp file))
                       nil)
                     ))