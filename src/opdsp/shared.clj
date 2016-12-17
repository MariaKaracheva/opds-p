(ns opdsp.shared
  (:require
    [clj-yaml.core :as yaml]
    [monger.core :as mg]
    [monger.collection :as mc]
    [monger.operators :refer :all]
    [lock-key.core :refer [decrypt decrypt-as-str decrypt-from-base64]]
    )
  (:import (java.io File)))

(def settingsPath (clojure.string/join "/" [ (java.lang.System/getenv "HOME") ".opds-p"]))

(def app-settings  (let [file (clojure.string/join "/" [settingsPath "app-settings.yaml"])]
                     (if (.exists (File. file))
                       (yaml/parse-string (slurp file))
                       (throw (Exception. (str "file" " app-settings.yaml " "was not found in " settingsPath))))
                     ))


(def mongodb (delay (let [conn (mg/connect)]
                      (mg/get-db conn "opds-p"))))

(defn loadSettings [^String user] (mc/find-one-as-map @mongodb "userSettings" {:login user} ))

(def ^:dynamic *settings*)

(defn key [] (let [settingsKey (:key *settings*)]
               (decrypt-from-base64 settingsKey "9qPBq1kFkOfPy5w9")
               ))