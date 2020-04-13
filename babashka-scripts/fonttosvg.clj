;; For a given directory of raw font files (ttf or otf)
;; Generate folders titled eg-font-name
;; Convert each gryph in EGFontName.ttf to an svg in eg-font-name/a_EGFontname.svg
;; And finally create a meta file that serves as an index

(def args *command-line-args*)

(when-not (= 3 (count args))
  (println "Invalid arguments")
  (println "Usage:" "bb fonttosvg.clj <serif-source-dir> <sans-serif-source-dir> <sink-dir>")
  (println "Note: The paths should be absoloute and not have trailing slashes")
  (System/exit 1))

(defn bash [command]
  (shell/sh "bash" "-c"
            command))

(defn font->svg [font-path font-file-name source-dir]
  (println "Processing" font-file-name "in" font-path)
  (let [command (str "cd " font-path
                     "; fontforge -lang=ff -c 'Open(\"" source-dir "/" font-file-name
                     "\"); SelectWorthOutputting(); foreach Export(\"svg\"); endloop;'" )]
    ;;(println command)
    (bash command)
    ))

(defn list-files [dir]
  (->> dir
       io/file
       file-seq
       (filter #(.isFile %))))

(defn process-font-file [src-dir sink-dir index-atom family f]
  (let [raw-name (.getName f)
        [_ basename] (re-matches #"(.*).[t|o]tf" raw-name)
        normalized-name (when basename
                          (-> basename
                              str/lower-case
                              (str/replace #"_" "-")))
        font-path (str sink-dir "/" normalized-name)]

    (when basename
      (.mkdir (java.io.File. font-path))
      ;; if path doesn't exist, vectors will be spit in dir of execution
      (wait/wait-for-path font-path)

      (swap! index-atom conj {:family family
                              :normalized-name normalized-name
                              :base-name basename
                              :raw raw-name})
      (font->svg (str sink-dir "/" normalized-name)
                 raw-name src-dir))
    ))


(let [index-atom (atom [])
      [serif-dir sans-serif-dir sink-dir] args]

  (println "Generating svgs...")
  (doall (pmap
          (partial process-font-file serif-dir sink-dir index-atom :serif)
          (list-files serif-dir)))

  (doall (pmap
          (partial process-font-file sans-serif-dir sink-dir index-atom :sans-serif)
          (list-files sans-serif-dir)))

  (println "Writing index...")
  (spit (str sink-dir "/index.json")
        (json/generate-string @index-atom {:pretty true}))
  )

