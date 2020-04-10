(def args *command-line-args*)

(when-not (= 1 (count args))
  (println "Invalid arguments")
  (println "Usage: bb vectorindex.clj <source-dir>")
  (System/exit 1))

(defn ls-raw [dir]
  (-> dir
      io/file
      file-seq))

(defn ls-dirs [dir]
  (->> dir
       ls-raw
       (filter #(.isDirectory %))
       (map #(.getPath %))))

(defn ls-files [dir]
  (->> dir
       ls-raw
       (filter #(.isFile %))
       (map #(.getName %))))

(defn write-index [font-dir]
  (println "Writing index at " font-dir)
  (spit (str font-dir "/" "index.edn")
        (prn-str (ls-files font-dir))))

(let [[source-dir] args
      font-folders (ls-dirs source-dir)]
  (doall
   (pmap write-index font-folders)))
