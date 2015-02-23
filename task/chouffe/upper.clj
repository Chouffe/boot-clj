(ns chouffe.upper
  (require
    [boot.core :refer :all]
    [boot.util :as util]
    [boot.pod :as pod]
    [clojure.java.io :as io]))

(defn lc->uc-path
  [path]
  (.replaceAll path "\\.lc$" ".uc"))

(deftask options
  "Playing with Options"
  [d demo OPTARG kw "The demo option."
   p props KEY:VAL {kw str} "The properties map."]
  (with-pre-wrap fileset
    (util/info (pr-str props))
    fileset))

(deftask upper
  []
  (let [tmp   (temp-dir!)
        state (atom nil)
        pod   (pod/make-pod (get-env))]
    (with-pre-wrap fileset
      (empty-dir! tmp)
      (doseq [lc-tmp (->> fileset
                          (fileset-diff @state)
                          input-files
                          (by-ext [".lc"]))
              :let [lc-file (tmpfile lc-tmp)
                    lc-path (tmppath lc-tmp)
                    uc-file (io/file tmp (lc->uc-path lc-path))]]
        (io/make-parents uc-file)
        (util/info "Compiling %s...\n" lc-path)
        (pod/with-eval-in pod
          (require '[chouffe.upper-impl :refer :all])
          (compile-lc ~(.getPath lc-file) ~(.getPath uc-file))))
      (reset! state fileset)
      (-> fileset (add-resource tmp) commit!))))

(deftask ntimes
  [t times N int "The number of times to do it."]
  (fn middleware [next-handler]
    (fn handler [fileset]
      (dotimes [_ times]
        (next-handler fileset))
      fileset)))

