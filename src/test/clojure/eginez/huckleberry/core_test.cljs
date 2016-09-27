(ns eginez.huckleberry.core-test
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.test :refer-macros [async deftest is testing]]
            [cljs.core.async :refer [put! take! chan <! >!] :as async]
            [cljs.pprint :as pp]
            [eginez.huckleberry.core :as maven]))

;com/cognitect/transit-java/0.8.313
(def test-dep {:group "com.cognitect" :artifact "transit-java" :version "0.8.313"})
(def test-dep2 {:group "cljs-bach" :artifact "cljs-bach" :version "0.2.0"})
(def test-dep3 {:group "reagent" :artifact "reagent" :version "0.6.0-alpha2"})
(def test-dep4 {:group "junit" :artifact "junit" :version "4.12"})
(def test-dep5 {:group "org.clojure" :artifact "clojure" :version "1.8.0"})
(def test-dep6 {:group "commons-logging" :artifact "commons-logging" :version "1.1"
                :exclusions [{:group "avalon-framework" :artifact "avalon-framework" :version "4.1.3"}]})

(def test-url (maven/create-urls-for-dependency (:maven-central maven/repos) test-dep))

(deftest create-url
  (let [urls (maven/create-urls-for-dependency (:local maven/repos) test-dep)]
    (assert (-> urls first coll? not))))

;(deftest create-url-repos
;  (let [urls (maven/create-urls-for-dependency (:clojars repos) test-dep)]
;    (assert (-> urls first coll?))))

(deftest test-resolve-single
  (async done
    (go
      (let [[status d] (<! (maven/resolve test-dep5 :repositories (vals maven/repos)))]
        (pp/pprint d)
        (done)))))

(deftest test-resolve-all-single
  (async done
    (go
      (let [[status d] (<! (maven/resolve-all [test-dep5] :repositories (vals maven/repos)))]
        (pp/pprint d)
        (done)))))

(deftest test-resolve-all-single2
  (async done
    (go
      (let [[status d] (<! (maven/resolve-all [test-dep3] :repositories (vals maven/repos)))]
        (pp/pprint d)
        (done)))))

(deftest test-resolve-all-single-with-exclusion
  (async done
    (go
      (let [[status d] (<! (maven/resolve-all [test-dep6] :repositories (vals maven/repos)))]
        (pp/pprint d)
        (done)))))

;(deftest test-resolve1
;  (async done
;    (go
;      (let [[status d] (<! (maven/resolve test-dep6 :repositories (vals maven/repos)))]
;        (is status true)
;        (is '#{{:group "commons-logging",
;                :artifact "commons-logging",
;                :version "1.1"}
;               {:group "log4j", :artifact "log4j", :version "1.2.12"}
;               {:group "logkit", :artifact "logkit", :version "1.0.1"}
;               {:group "avalon-framework",
;                :artifact "avalon-framework",
;                :version "4.1.3"}
;               {:group "javax.servlet", :artifact "servlet-api", :version "2.3"}}
;            d)
;        (done)))))

;(deftest test-resolve-dep1
;  (async done
;    (go
;      (let [[status dp] (<! (maven/resolve-dependencies :coordinates '[[commons-logging "1.2"]]
;                                                        :retrieve false
;                                                        :local-repo nil))]
;        (println dp)
;        (done)
;        ))))

;(comment
;(deftest test-resolve-dep2
;  (let [deps '[[commons-logging "1.1"]
;               [log4j "1.2.15" :exclusions [[javax.mail/mail :extension "jar"]
;                                            [javax.jms/jms :classifier "*"]
;                                            com.sun.jdmk/jmxtools
;                                            com.sun.jmx/jmxri]]]
;        ]
;    (async done
;      (go
;        (let [[status dp] (<! (maven/resolve-dependencies :coordinates deps
;                                                      :retrieve false
;                                                      :local-repo tmp-local-repo-dir))]
;          (println dp)
;          (done)
;          ))))))
