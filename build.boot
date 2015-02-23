(set-env!
  :source-paths #{"src" "task"})

(require '[chouffe.upper :refer :all])

(deftask foo
  [a arg ARGVAL int "The argument"]
  (let [state (atom arg)]
    (fn middleware [next-handler]
      (fn handler [fileset]
        (println "Hello World!"
                 (swap! state inc))
        (next-handler fileset)))))

(deftask foo
  [a arg ARGVAL int "The argument"]
  (let [state (atom arg)]
    (with-pre-wrap fileset
      (println "Hello World!"
               (swap! state inc))
      fileset)))

(deftask bar
  []
  (comp (foo :arg 1)
        (foo :arg 2)))

(task-options!
  ntimes {:times 2}
  speak {:theme "ordinance"})

(deftask dev
  []
  (comp (watch)
        (speak)
        (ntimes)
        (upper)))
