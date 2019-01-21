(ns igv-client.core-test
  (:require [clojure.java.io :as io]
            [clojure.test :refer [deftest is are]]
            [igv-client.core :as igv])
  (:import [java.io Closeable PipedWriter PipedReader]))

(defrecord MockClient [in out result]
  Closeable
  (close [this]
    (.close in)
    (.close out)))

(defn mock-client
  ([expected]
   (mock-client expected "OK"))
  ([expected response]
   (let [writer-in (PipedWriter.)
         reader-in (PipedReader. writer-in)
         writer-out (PipedWriter.)
         reader-out (PipedReader. writer-out)
         client (->MockClient (io/reader reader-out) (io/writer writer-in) (atom nil))]
     (future
       (binding [*in* (io/reader reader-in)
                 *out* (io/writer writer-out)]
         (let [res (if (= (read-line) expected) response "ERROR")]
           (reset! (:result client) res)
           (println res))))
     client)))

(deftest version-cmd-test
  (with-open [c (mock-client "version" "2.0.0")]
    (is (= "2.0.0" (igv/version c)))))

(deftest ordinary-cmd-test
  (are [cmd expected] (with-open [c (mock-client expected)]
                        (-> c cmd)
                        (= "OK" @(:result c)))
    (igv/reset) "reset"
    (igv/load-file "/path/to/file") "loadfile /path/to/file"
    (igv/remove "track1") "remove track1"
    (igv/genome "genome") "genome genome"
    (igv/goto "chr1:123456") "goto chr1:123456"
    (igv/goto "chr1" 123456) "goto chr1:123456"
    (igv/goto "chr1" 123456 123789) "goto chr1:123456-123789"
    (igv/goto-track "track1") "gototrack track1"
    (igv/set-sleep-interval! 1000) "setsleepinterval 1000"
    (igv/set-snapshot-dir! "/path/to/dir") "snapshotdirectory /path/to/dir"
    (igv/snapshot) "snapshot"
    (igv/zoom-in) "zoomin"
    (igv/zoom-out) "zoomout"
    (igv/collapse) "collapse"
    (igv/collapse "track1") "collapse track1"
    (igv/expand) "expand"
    (igv/expand "track1") "expand track1"
    (igv/squish) "squish"
    (igv/squish "track1") "squish track1"
    (igv/bring-to-front!) "tofront"))
