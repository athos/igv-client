(ns igv-client.core
  (:refer-clojure :exclude [send load-file remove])
  (:require [clojure.java.io :as io])
  (:import [java.io Closeable]
           [java.net Socket]))

(defrecord IGVClient [^Socket socket in out]
  Closeable
  (close [this]
    (.close socket)))

(defn ^IGVClient connect [^String host ^long port]
  (let [socket (Socket. host port)
        out (io/writer (.getOutputStream socket))
        in (io/reader (.getInputStream socket))]
    (->IGVClient socket in out)))

(defn recv [{:keys [in]}]
  (binding [*in* in]
    (read-line)))

(defn send [{:keys [out]} cmd & args]
  (binding [*out* out]
    (apply println (name cmd) args)))

(defn version [client]
  (send client "version")
  (recv client))

(defn request [client cmd & args]
  (apply send client cmd args)
  (let [resp (recv client)]
    (when-not (= resp "OK")
      (throw (ex-info resp {:command (vec (cons cmd args))}))))
  client)

(defn- ->path [x]
  (.getPath (io/as-file x)))

(defn reset [client]
  (request client :reset))

(defn load-file [client file]
  (request client :loadfile (->path file)))

(defn remove [client track]
  (request client :remove track))

(defn genome [client genome]
  (request client :genome genome))

(defn goto
  ([client locus]
   (request client :goto locus))
  ([client chr pos]
   (goto client (str (name chr) \: pos)))
  ([client chr start end]
   (goto client chr (str start \- end))))

(defn goto-track [client track]
  (request client :gototrack track))

(defn set-sleep-interval! [client msec]
  (request client :setsleepinterval msec))

(defn set-snapshot-dir! [client dir]
  (request client :snapshotdirectory (->path dir)))

(defn snapshot [client]
  (request client :snapshot))

(defn zoom-in [client]
  (request client :zoomin))

(defn zoom-out [client]
  (request client :zoomout))

(defn collapse
  ([client]
   (request client :collapse))
  ([client track]
   (request client :collapse track)))

(defn expand
  ([client]
   (request client :expand))
  ([client track]
   (request client :expand track)))

(defn squish
  ([client]
   (request client :squish))
  ([client track]
   (request client :squish track)))

(defn bring-to-front! [client]
  (request client :tofront))
