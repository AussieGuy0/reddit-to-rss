(ns reddit-to-rss.core-test
  (:require [clojure.test :refer [deftest is testing]]
            [reddit-to-rss.core :refer [parse-as-hickory parse-posts post-to-rss-item]]))

(defn read-html-file [filename]
  (slurp (str "resources/" filename)))

(deftest parse-as-hickory-test
  (testing "parse-as-hickory"
    (let [html "<html><head></head><body><p>Hello, World!</p></body></html>"
          parsed-html (parse-as-hickory html)]
      (is (map? parsed-html) "The result should be a map")
      (is (= :html (-> parsed-html :content first :tag)) "The root tag should be html")
      (is (= "Hello, World!" (-> parsed-html
                                 :content first  ; Selecting HTML
                                 :content second ; Selecting body
                                 :content first  ; Selecting p
                                 :content first)) "The content of the <p> tag should be 'Hello, World!'"))))

(deftest parse-posts-test
  (testing "parse-posts"
    (let [html (read-html-file "test.html")
          posts (parse-posts html)]
      (is (vector? posts) "Posts should be a vector")
      (is (= 4 (count posts)) "Should have 4 posts")
      (is (=
           {:title "How much of a pro/con is dynamic typing in clojure?"
            :link "/r/Clojure/comments/1d6e7ex/how_much_of_a_procon_is_dynamic_typing_in_clojure/"
            :upvotes 24}
           (first posts)) "The first post should have the correct data")
      (is (=
           {:title "Which Typing System? Spec, Malli, typedclojure?"
            :link "/r/Clojure/comments/1d6jfvu/which_typing_system_spec_malli_typedclojure/"
            :upvotes 12}
           (second posts)) "The second post should have the correct data"))))

(deftest post-to-rss-item-test
  (testing "post-to-rss-item"
    (is (=
         {:title "title"
          :link "https://old.reddit.com/r/subreddit/1"
          :description "5 upvotes"}
         (post-to-rss-item {:title "title" :link "/r/subreddit/1" :upvotes 5})))))
