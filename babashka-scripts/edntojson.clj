;; Copies all edn files in a dir to their json counterpart
;; The edn files in sub directory are not touched

(def args *command-line-args*)

(when-not (= 1 (count args))
  (println "Invalid arguments")
  (println "Usage: bb edntojson.clj <source-dir>")
  (System/exit 1))

(defn ls-raw [dir]
  (-> dir
      io/file
      file-seq))

(defn ls-edn-files [dir]
  (->> dir
       ls-raw
       (filter #(.isFile %))
       (filter #(re-matches #".*.edn" (.getName %)))
       (map #(.getPath %))))

(defn edn->json [path]
  (let [json-path (str/replace path #".edn" ".json")]
    (println path :---> json-path)
    (spit json-path
          (json/generate-string
           (edn/read-string (slurp path))
           {:pretty true}))))

(let [[source-dir] args
      ednz (ls-edn-files source-dir)]
  (doall
   (pmap edn->json ednz)))
