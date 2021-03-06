(ns opdsp.shared
  (:require
    [clj-yaml.core :as yaml]
    [monger.core :as mg]
    [monger.collection :as mc]
    [monger.operators :refer :all]
    [lock-key.core :refer [decrypt decrypt-as-str decrypt-from-base64]]
    )
  (:import (java.io File)
           (java.util Map)))

(def settingsPath (clojure.string/join "/" [ (java.lang.System/getenv "HOME") ".opds-p"]))

(def app-settings  (let [file (clojure.string/join "/" [settingsPath "app-settings.yaml"])]
                     (if (.exists (File. file))
                       (yaml/parse-string (slurp file))
                       (throw (Exception. (str "file" " app-settings.yaml " "was not found in " settingsPath))))
                     ))


(def mongodb (delay (let [conn (mg/connect)]
                      (mg/get-db conn "opds-p"))))

(defn loadUserSettings [^String user]
  {:pre [(some? user)]}
  (mc/find-one-as-map @mongodb "userSettings" {:login user}))

(defn loadUserSettingsByCatalogAuth [^String login ^String password]
  {:pre [(some? login)]}
  (mc/find-one-as-map @mongodb "userSettings" {:catalog.auth.login login :catalog.auth.password password}))

(defn updateUserSettings [^String user ^Map data] (mc/upsert @mongodb "userSettings" {:login user} {$set data}))

(def ^:dynamic *userSettings*)

(defn yandex-key [] {:pre (some? *userSettings*)} (:key *userSettings*))