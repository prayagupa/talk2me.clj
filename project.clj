(defproject talk2me "0.1.0"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.8.0"]
                 [com.specs/specs-lib "1.0"]]

  :main ^:skip-aot talk2me.server
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})

