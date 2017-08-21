(ns talk2me.server
  (:import (java.io BufferedReader InputStreamReader OutputStreamWriter PrintWriter)
           (java.net ServerSocket)))

(defn talk2me-server []
  (let [server (new ServerSocket 9999)]
    (println "Server started at :9999")
    (while true
      (do
        (let [connection (.accept server)
              output-stream (.getOutputStream connection)
              response (new PrintWriter output-stream)]
          (println (str "[INFO] connection established from " (.getInetAddress connection) ":" (.getPort connection)))
          (.println response "hello-clj")
          (.flush response)
          (.close connection))))))

(def my-server (talk2me-server))

