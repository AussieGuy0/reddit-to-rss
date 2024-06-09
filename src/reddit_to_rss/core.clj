(ns reddit-to-rss.core
  (:require [hickory.core :as hickory]
            [hickory.select :as select]
            [clj-rss.core :as rss]))

(def reddit-base-url "https://old.reddit.com")

(defn subreddit-url
  [subreddit]
  (str reddit-base-url "/r/" subreddit))

(defn subreddit-top-url
  [subreddit]
  (str reddit-base-url "/r/" subreddit "/top"))

(defn fetch-posts
  [subreddit]
  (slurp (subreddit-top-url subreddit)))

(defn parse-as-hickory
  [html]
  (-> html
      (hickory/parse)
      (hickory/as-hickory)))

(defn parse-posts
  [subreddit-html]
  (let [parsed-html (parse-as-hickory subreddit-html)]
    (->> (select/select (select/class "thing") parsed-html)
         (mapv (fn [post]
                 {:title (-> (select/select (select/attr "data-event-action" #(= "title" %)) post)
                             first
                             :content
                             first)
                  :link (-> post
                            :attrs
                            :data-permalink)
                  :upvotes (-> post
                               :attrs
                               :data-score
                               Integer/parseInt)})))))

(defn fetch-and-parse-posts [subreddit]
  (parse-posts (fetch-posts subreddit)))

(defn post-to-rss-item
  [post]
  {:title (:title post)
   :link (str reddit-base-url (:link post))
   :description (str (:upvotes post) " upvotes")})

(defn posts-to-rss
  [subreddit posts]
  (let [rss-items (mapv post-to-rss-item posts)]
    (rss/channel-xml {:title (str subreddit " subreddit") :link (subreddit-url subreddit) :description "reddit"}
                     rss-items)))

(defn reddit-to-rss
  [subreddit outfile]
  (let [posts (fetch-and-parse-posts subreddit)]
    (spit outfile (posts-to-rss subreddit posts))))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (print "hello"))

(comment
  (reddit-to-rss "clojure" "bla.xml")
  ;
  )


