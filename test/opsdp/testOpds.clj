(ns opsdp.testOpds
  (:require [clojure.test :refer :all]
            [opdsp.opds :as o]))



(deftest nt-ABC.Pdf  (is (= {:displayname "ABC" :type "application/pdf"} (o/nameAndType {:displayname "ABC.pdf"}))))
(deftest nt-ABC.epub  (is (= {:displayname "ABC" :type "application/epub+zip"} (o/nameAndType {:displayname "ABC.epuB"}))))
(deftest nt-ABC.exe (is (= {:displayname "ABC.exe" :type "application/octet-stream"} (o/nameAndType {:displayname "ABC.exe"}))))


