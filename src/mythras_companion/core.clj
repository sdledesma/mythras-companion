(ns mythras-companion.core
  (:require [cheshire.core :as json]
             [clj-http.client :as http]))

(def base-url "http://skoll.xyz/mythras_eg/")

(defn get-templates []
  (let [raw (http/get (str base-url "index_json") {:content-type :application/json})]
    (json/parse-string (:body raw) true)))

(defn- contains-all?
  "returns true if set1 contains all members of set2"
  [set1 set2]
  (= set2 (clojure.set/intersection set1 set2)))


(defn find-templates-by-tags [template-seq set-of-tags-as-string]
  (let [filter-fn (fn [template]
                    (contains-all? (apply hash-set
                                          (:tags template))
                                   set-of-tags-as-string))]
    (filter filter-fn template-seq)))

(defn find-template-by-tag [template-seq tag-as-string]
  (find-templates-by-tags template-seq (hash-set tag-as-string)))

(defn generate-enemies-by-id [id qty]
  (let [raw (http/get (str base-url "generate_enemies_json/?id=" id "&amount=" qty)
                      {:content-type :application/json})]
    (json/parse-string (:body raw) true)))

(defn generate-enemy-by-id [id]
  (first (generate-enemies-by-id id 1)))

(defn get-all-tags [template-seq]
  (reduce (fn [coll template]
             (into coll (apply hash-set (:tags template))))
          #{}
          template-seq))

(defn extract-weapons-from-npc [npc]
  (let [combat-styles (:combat_styles npc)
        weapons       (map :weapons combat-styles)]
    (flatten weapons)))

(defn extract-combat-style-from-npc [npc]
  (:combat_styles npc))
