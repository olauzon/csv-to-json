(ns csv-to-json.core-test
  (:require [clojure.test     :refer :all]
            [csv-to-json.core :refer :all]))

(def fixture-input-row-00
  ["d70173c0-1519-0d55-7b04-ab9756cf7cc3"
   "79"
   "Sun Jun 12 14:33:33 EDT 2011"
   "2013-09-10T20:22:32.520Z"
   "a"
   "true"
   "31271"
   "1778736097"
   "8491964968156714474"
   "=T^G0"
   "3473164520662945660"
   "10"])

(def fixture-input-string-00
  "1:user_uuid")

(def fixture-input-string-01
  "1:event.uuid,2:user.id:int,4:event.timestamp,6:admin:bool")

(def fixture-input-string-02
  "1:user.name,3:user.dob,5:created_at,7:session.login.expired:bool")

(deftest test-extract-key-00
  (testing "Convert dot notation to Clojure list"
    (is (= 
         '("user" "session" "created_at") 
         (extract-key "user.session.created_at")))))

(deftest test-mappings-00
  (testing "Converts an input string (00) to a CSV to JSON mapping"
    (is (=
      '({ :col 0
          :key-path ["user_uuid"] })
      (mappings fixture-input-string-00)))))

(deftest test-mappings-01
  (testing "Converts an input string (01) to a CSV to JSON mapping"
    (is (=
      '({ :col 0
          :key-path ["event" "uuid"] }
        { :col 1
          :key-path ["user" "id"]
          :type "int" }
        { :col 3
          :key-path ["event" "timestamp"] }
        { :col 5
          :key-path ["admin"]
          :type "bool" })
      (mappings fixture-input-string-01)))))

(deftest test-mappings-02
  (testing "Converts an input string (02) to a CSV to JSON mapping"
    (is (=
      '({ :col 0
          :key-path ["user" "name"] }
        { :col 2
          :key-path ["user" "dob"] }
        { :col 4
          :key-path ["created_at"] }
        { :col 6
          :key-path ["session" "login" "expired"]
          :type "bool" })
      (mappings fixture-input-string-02)))))

(deftest test-map-row-00
  (testing "Converts a CSV row to a Clojure map"
    (is (= 
      {
        "user_uuid" "d70173c0-1519-0d55-7b04-ab9756cf7cc3"
      }
      (map-row fixture-input-row-00 (mappings fixture-input-string-00))))))

(deftest test-map-row-02
  (testing "Converts a CSV row to a Clojure map"
    (is (= 
      {
        "event" {
          "uuid"      "d70173c0-1519-0d55-7b04-ab9756cf7cc3"
          "timestamp" "2013-09-10T20:22:32.520Z"
        }
        "user" {
          "id" 79
        }
        "admin" true
      }
      (map-row fixture-input-row-00 (mappings fixture-input-string-01))))))
