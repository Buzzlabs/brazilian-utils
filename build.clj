(ns build
  (:require
   [clojure.tools.build.api :as b]))

(def lib 'br.com.buzzlabs/brazilian-utils)
(def version (format "0.1.%s" (b/git-count-revs nil)))
(def class-dir "target/classes")
(def jar-file "target/brazilian-utils.jar")

(def basis (delay (b/create-basis {:project "deps.edn"})))

(defn clean [_]
  (b/delete {:path "target"}))

(defn jar [_]
  (b/write-pom {:class-dir class-dir
                :lib lib
                :version version
                :basis @basis
                :src-dirs ["src"]
                :scm {:tag (str "v" version)}})
  (b/copy-dir {:src-dirs ["src" "resources"]
               :target-dir class-dir})
  (b/jar {:class-dir class-dir
          :jar-file jar-file}))
