(ns talk2me.server
  (:import (java.io BufferedReader InputStreamReader OutputStreamWriter PrintWriter)
           (java.net ServerSocket InetAddress))
   (:require [clojure.java.io :as io]))

(def talk2me-data "talk2me.data")

(defn talk2me-server []
  (let [server (new ServerSocket 9999)
       localhost (InetAddress/getLocalHost)]
    (println (str "[INFO] Server started at " (.getHostAddress localhost) ":9999"))
    (while true
      (do
        (let [connection (.accept server)
              output-stream (.getOutputStream connection)
              response (new PrintWriter output-stream)]
          (println (str "[INFO] connection established from " (.getInetAddress connection) ":" (.getPort connection)))
          
          (with-open [data-stream (io/reader talk2me-data)]
            (doseq [line (line-seq data-stream)]
            (.println response line )))

          (.flush response)
          (.close connection))))))

(def my-server (talk2me-server))

