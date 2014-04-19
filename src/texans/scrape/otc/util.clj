(ns texans.scrape.otc.util
  (:require [net.cgrand.enlive-html :as html]))

(defn fetch-url [url]
  (html/html-resource (java.net.URL. url)))

(defn table-rows
  "Given a table of enlive tags, extracts a sequence of table body rows"
  [table]
    (html/select table [:tbody :tr]))

(defn table-row-cols
  "Given a row of enlive tags, extracts a sequence of columns"
  [row]
    (html/select row [:td]))

(defn extract-col [col sel]
  (apply str (apply :content (html/select col sel))))

(defn selectors-from-spec
  "get a list of selectors from a spec"
  [spec]
    (map #(let [[_ sel] %] sel) (partition 2 spec)))

(defn keys-from-spec
  "get a list of keys from a spec"
  [spec]
    (map #(let [[k _] %] k) (partition 2 spec)))

(defn row-extractor
  "builds a function to extract column data using the given list of selectors"
  [sels]
    (fn [row]
      (let [cols (table-row-cols row)]
        (map extract-col cols sels))))

(defn row-map-builder
  "builds a function that builds a map of row data using the provided spec"
  [spec]
    (fn [row]
      (let [sels (selectors-from-spec spec)
            datakeys (keys-from-spec spec)
            fn-extract (row-extractor sels)
            col-data (fn-extract row)]
        (apply merge (map (fn [data k]
               {k data}) col-data datakeys)))))

(defn table->map
  "builds a map from the given table and spec"
  [table spec]
  (let [rows (table-rows table)
        fn-builder (row-map-builder spec)]
    (map fn-builder rows)))


