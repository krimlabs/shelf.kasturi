;; strips away font meta from generated glyph svgs
;; ie: $A_EBGaraomondRegular.svg becomes $A.svg

(def args *command-line-args*)

(when-not (= 1 (count args))
  (println "Usage: bb normalize_svg_name.clj <source-dir>")
  (System/exit 1))

(defn bash [command]
  ;;(println command)
  (shell/sh "bash" "-c" command))

(defn list-files [dir]
  (->> dir
       io/file
       file-seq
       (filter #(.isFile %))))

(defn list-dirs [dir]
  (->> dir
       io/file
       file-seq
       (filter #(.isDirectory %))))

(defn process-file [f]
  (let [path (.getPath f)
        name (.getName f)
        new-name (-> name
                   (str/split #"_")
                   first
                   (str ".svg")
                   )]
    (when (and  (str/ends-with? path ".svg")
                (not (str/ends-with? new-name ".svg.svg")))
      (.renameTo (java.io.File. path)
                 (java.io.File. (-> path
                                    (str/split #"/")
                                    drop-last
                                    (->> (str/join "/"))
                                    (str "/" new-name))))
      )))

(defn process-dir [d]
  (let [all-files (list-files (.getPath d))]
    (doall
     (pmap process-file all-files))))

(let [[source-dir] args
      font-dirs (list-dirs source-dir)]
  (doall
   (pmap process-dir font-dirs))
  :done)

