(ns opdsp.opds
  (:require [clojure.data.xml :refer [element]])
  (:require [clojure.string :refer [ends-with? lower-case]]))



(def ^:dynamic *pathPrefix* "")

(def dirprefix "dir")
(def fileprefix "file")

(def supportedMimes {".epub" "application/epub+zip"
                     ".mobi" "application/x-mobipocket-ebook"
                     ".pdf"  "application/pdf"
                     ".djvu" "image/vnd.djvu"
                     ".fb2"  "application/fb2+zip"})

(defn fileType [filename] (let [lowercased (lower-case filename)
                                ext (->> supportedMimes keys
                                         (filter #(ends-with? lowercased %))
                                         (first))]
                            (or (some->> ext (get supportedMimes)) "application/octet-stream")
                            ))

(defn nameAndType [{filename :displayname}] (let [lowercased (lower-case filename)
                                                  ext (->> supportedMimes keys
                                                           (filter #(ends-with? lowercased %))
                                                           (first))]
                                              (if ext
                                                {:displayname (subs filename 0 (- (count filename) (count ext)))
                                                 :type        (get supportedMimes ext)}
                                                {:displayname filename
                                                 :type        "application/octet-stream"})
                                              ))

(defn entryTagData [[name entries]]
  (element "entry" {}

           (concat
             [(element "title" {} name)
              (element "content" {:type "html"})]
             (map (fn [entry] (element "link" (if (entry :collection)
                                                {
                                                 :type "application/atom+xml; profile=opds-catalog; kind=acquisition"
                                                 :kind "acquisition"
                                                 :rel  "subsection"
                                                 :href (str *pathPrefix* "/" dirprefix (entry :href))
                                                 }
                                                {:type (entry :type)
                                                 :kind "acquisition"
                                                 :rel  "http://opds-spec.org/acquisition"
                                                 :href (str *pathPrefix* "/" fileprefix (entry :href))
                                                 }
                                                ))) entries))))

(defn documentTagData [entries]
  (
    element "feed"
            {
             :xmlns            "http://www.w3.org/2005/Atom"
             :xmlns:dcterms    "http://purl.org/dc/terms/"
             :xmlns:pse        "http://vaemendis.net/opds-pse/ns"
             :xmlns:opds       "http://opds-spec.org/2010/catalog"
             :xml:lang         "en"
             :xmlns:opensearch "http://a9.com/-/spec/opensearch/1.1/"
             }
            (concat [(element "title" {} "Books")]
                    (->> entries
                         (map #(merge % (nameAndType %)))
                         (group-by #(% :displayname))
                         (sort-by #(identity [(-> % (second) (first) (:collection) (not)) (first %)]))
                         (map entryTagData)))))


