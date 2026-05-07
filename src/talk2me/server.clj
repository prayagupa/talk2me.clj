(ns talk2me.server
  (:import (java.nio.channels ServerSocketChannel SocketChannel Selector SelectionKey)
           (java.nio ByteBuffer)
           (java.nio.charset StandardCharsets)
           (java.net InetSocketAddress InetAddress))
  (:require [clojure.java.io :as io]))

(def talk2me-data "talk2me.data")

(defn write-to-channel [^SocketChannel ch ^String text]
  (let [buf (ByteBuffer/wrap (.getBytes text StandardCharsets/UTF_8))]
    (while (.hasRemaining buf)
      (.write ch buf))))

(defn drain-request [^SocketChannel ch]
  (let [buf (ByteBuffer/allocate 4096)]
    (.read ch buf)))

(defn handle-client [^SocketChannel client-ch]
  (try
    (.configureBlocking client-ch true)
    (drain-request client-ch)
    (write-to-channel client-ch "HTTP/1.0 200 OK\r\nContent-Type: text/plain\r\n\r\n")
    (with-open [data-stream (io/reader talk2me-data)]
      (doseq [line (line-seq data-stream)]
        (write-to-channel client-ch (str line "\n"))))
    (finally
      (.close client-ch))))

(defn talk2me-server []
  (let [selector  (Selector/open)
        server-ch (doto (ServerSocketChannel/open)
                    (.configureBlocking false)
                    (.bind (InetSocketAddress. 9999)))
        localhost (InetAddress/getLocalHost)]
    (.register server-ch selector SelectionKey/OP_ACCEPT)
    (println (str "[INFO] Server started at " (.getHostAddress localhost) ":9999"))
    (while true
      (.select selector)
      (let [selected-keys (.selectedKeys selector)]
        (doseq [^SelectionKey key selected-keys]
          (when (.isAcceptable key)
            (when-let [^SocketChannel client-ch (.accept server-ch)]
              (println (str "[INFO] connection established from "
                            (.. client-ch socket getInetAddress)
                            ":" (.. client-ch socket getPort)))
              (future (handle-client client-ch)))))
        (.clear selected-keys)))))

(defn -main [& _args]
  (talk2me-server))

(def my-server (future (talk2me-server)))
