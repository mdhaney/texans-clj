(ns texans.scrape.otc.core
  (:require [net.cgrand.enlive-html :as html]
            [texans.scrape.otc.core :refer [fetch-url table->map]]))

(def ^:dynamic *base-url* "http://overthecap.com/")

;; team contracts
(defn teamcontract-url [team]
  (str *base-url* "teamcontract.php?Team=" team))

(def contracts-spec [:player [:td :a]
                     :position [:td]
                     :total-value [:td]
                     :apy [:td]
                     :guarantees [:td]
                     :guarantee-per-year [:td]
                     :guarantee-pct [:td]
                     :free-agency [:td]])

(defn fetch-contracts-table [team]
  (let [table (fetch-url (teamcontract-url team))]
    (html/select table [:div#content-main :table.sortable])))

(defn fetch-team-contracts [team]
  (let [table (fetch-contracts-table team)]
    (table->map table contracts-spec)))

;; sample call to fetch team contract data
;(clojure.pprint/pprint (fetch-team-contracts "Texans"))

;; team cap
(defn teamcap-url [team year]
  (str *base-url* "teamcap.php?Team=" team "&Year=" year))

(def teamcap-spec   [:player [:td :a]
                     :base-salary [:td]
                     :prorated-bonus [:td]
                     :roster-bonus [:td]
                     :workout-bonus [:td]
                     :cap-number [:td]
                     :dead-money [:td]
                     :cap-savings [:td]])

(defn fetch-teamcap-table [team]
  (let [table (fetch-url (teamcap-url team "2013"))]
    (html/select table [:div#content-main [:table.sortable html/first-of-type]])))

(defn fetch-teamcap [team]
  (let [table (fetch-teamcap-table team)]
    (table->map table teamcap-spec)))

;(clojure.pprint/pprint (fetch-teamcap "Texans"))

