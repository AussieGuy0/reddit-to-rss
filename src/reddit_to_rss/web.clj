(ns reddit-to-rss.web
  (:gen-class)
  (:require [chime.core :as chime]
            [reddit-to-rss.core :as reddit]
            [reitit.ring :as retit]
            [ring.adapter.jetty :as ring-jetty]))

(import [java.time LocalTime Period ZonedDateTime ZoneId LocalDate])

; TODO: Have this as config.
(def subreddits
  ["clojure", "programming"])

(defn generate-periodic-seq
  []
  (chime/periodic-seq (-> (ZonedDateTime/of (LocalDate/now) (LocalTime/of 8 0) (ZoneId/of "UTC"))
                          .toInstant)
                      (Period/ofDays 1)))

(defn run-reddit-to-rss
  [subreddits]
  (run! #(reddit/reddit-to-rss % (str "public/" % ".xml")) subreddits))

(defn start-schedule
  [subreddits]
  (chime/chime-at (generate-periodic-seq)
                  (fn []
                    (run-reddit-to-rss subreddits))))

(def routes
  (retit/ring-handler
   (retit/router
    [["/public/*" (retit/create-file-handler)]])
   (retit/create-default-handler)))

(defn server []
  (ring-jetty/run-jetty #'routes {:join? false, :port 3000}))

(defn -main
  [& args]
  (start-schedule subreddits)
  (server))

(comment
  (server) ;
  (start-schedule ["clojure"])
  (run-reddit-to-rss ["clojure", "programming"])
;
  )
