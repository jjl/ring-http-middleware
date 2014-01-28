(ns ring.http.middleware_test
  (:use clojure.test)
  (:require [ring.http.middleware :refer [wrap-http-params wrap-keyword-http-params]]))

(defn sk [i]
  (sort (keys i)))

(defn thrower [a] (throw (Exception. "An Exception")))

(deftest test-wrap-http-params
  (let [wrap #(wrap-http-params %)
        wrapped #((wrap identity) %)
        skw #(sk (wrapped %))
        basic {:a 1 :b 2}
        basic2 {:c 3 :d 4}
        basic3 {:a 1 :b 2 :c 3 :d 4}]
    (let [sample1 {}
          sample2 {:query-params {}}
          sample3 {:form-params {}}
          sample4 {:query-params basic}
          sample5 {:query-params basic :form-params {}}
          sample6 {:query-params {} :form-params basic}
          sample7 {:form-params basic}
          sample8 {:query-params basic :form-params basic2}
          sample9 {:query-params {} :form-params {}}
          kw1 (sk basic)
          kw2 (sk basic2)
          kw3 (sk basic3)]
      (testing "key added"
        (is (= (skw sample1)
               '(:http-params)))
        (is (= (skw sample2)
               (skw sample4)
               '(:http-params :query-params)))
        (is (= (skw sample3)
               (skw sample7)
               '(:form-params :http-params)))
        (is (= (skw sample5)
               (skw sample6)
               (skw sample8)
               (skw sample9)
               '(:form-params :http-params :query-params))))
      (testing "results"
        (is (= (keys ((wrapped sample2) :http-params))
               (keys ((wrapped sample3) :http-params))
               nil))
        (is (= (sk ((wrapped sample4) :http-params))
               (sk ((wrapped sample5) :http-params))
               (sk ((wrapped sample6) :http-params))
               (sk ((wrapped sample7) :http-params))
               kw1))
        (is (= (sk ((wrapped sample8) :http-params))
               kw3)))
      (testing "sources untouched"
        (is (= (keys ((wrapped sample2) :query-params))
               (keys ((wrapped sample3) :form-params))
               (keys ((wrapped sample5) :form-params))
               (keys ((wrapped sample6) :query-params))
               (keys ((wrapped sample9) :query-params))
               (keys ((wrapped sample9) :form-params))
               nil))
        (is (= (sk ((wrapped sample4) :query-params))
               (sk ((wrapped sample5) :query-params))
               (sk ((wrapped sample8) :query-params))
               (sk ((wrapped sample6) :form-params))
               (sk ((wrapped sample7) :form-params))
               kw1))
        (is (= (sk ((wrapped sample8) :form-params))
               kw2)))
      (testing "pass through"
        (is (thrown? Exception ((wrapped thrower))))))))

(deftest test-wrap-keyword-http-params
  (let [wrap wrap-keyword-http-params
        wrapped #((wrap identity) %)
        skw #(sk (wrapped %))
        basic {"a" 1 "b" 2}
        basic2 {"c" 3 "d" 4}
        kw1 '(:a :b)
        kw2 '(:c :d)]
    (let [sample1 {}
          sample2 {:query-params {}}
          sample3 {:form-params {}}
          sample4 {:query-params basic}
          sample5 {:query-params basic :form-params {}}
          sample6 {:query-params {} :form-params basic}
          sample7 {:form-params basic}
          sample8 {:query-params basic :form-params basic2}
          sample9 {:query-params {} :form-params {}}
          kb (sk basic)
          kb2 (sk basic2)]
      (testing "keys unmodified"
        (is (= (skw sample1)
               '()))
        (is (= (skw sample2)
               (skw sample4)
               '(:query-params)))
        (is (= (skw sample3)
               (skw sample7)
               '(:form-params)))
        (is (= (skw sample5)
               (skw sample6)
               (skw sample8)
               (skw sample9)
               '(:form-params :query-params))))
      (testing "empties"
        (is (= (keys ((wrapped sample2) :query-params))
               (keys ((wrapped sample3) :form-params))
               (keys ((wrapped sample5) :form-params))
               (keys ((wrapped sample6) :query-params))
               (keys ((wrapped sample9) :query-params))
               (keys ((wrapped sample9) :form-params))
               nil)))
      (testing "nonempties"
        (is (= (sk ((wrapped sample4) :query-params))
               (sk ((wrapped sample5) :query-params))
               (sk ((wrapped sample8) :query-params))
               (sk ((wrapped sample6) :form-params))
               (sk ((wrapped sample7) :form-params))
               kw1))
        (is (= 
               (sk ((wrapped sample8) :form-params))
               kw2)))
      (testing "pass through"
        (is (thrown? Exception ((wrapped thrower))))))))
