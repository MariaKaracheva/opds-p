(ns hello-world.opds
  (:require [clojure.xml :as xml]))

;<feed xmlns="http://www.w3.org/2005/Atom" xmlns:dcterms="http://purl.org/dc/terms/"
;xmlns:pse="http://vaemendis.net/opds-pse/ns" xmlns:opds="http://opds-spec.org/2010/catalog" xml:lang="en"
;xmlns:opensearch="http://a9.com/-/spec/opensearch/1.1/">
;<id>1</id>
;<title>Books - 11 items</title>
;<updated>2016-03-25T16:09:41+02:00</updated>
;<icon>/theme/favicon.ico</icon>
;<author>
;<name>Ubooquity server</name>
;<uri>http://vaemendis.net/ubooquity</uri>
;</author>
;<link type="application/atom+xml; profile=opds-catalog; kind=navigation" rel="self" href="/opds-books/1/"/>
;<link type="application/atom+xml; profile=opds-catalog; kind=navigation" rel="start" href="/opds-books/"/>

(defn entryTagData [entry]
  {:tag "entry" :content
        [
         {:tag "title" :content [(entry :displayname)]}
         {:tag "content" :attrs {:type "html"}}
         {:tag "link" :attrs
               {:type "application/atom+xml; profile=opds-catalog; kind=acquisition"
                :kind "acquisition"
                :rel  "subsection"
                :href (entry :href)
                }
          }]})

(defn documentTagData [entries]
  {
   :tag     "feed"
   :attrs
            {
             :xmlns            "http://www.w3.org/2005/Atom"
             :xmlns:dcterms    "http://purl.org/dc/terms/"
             :xmlns:pse        "http://vaemendis.net/opds-pse/ns"
             :xmlns:opds       "http://opds-spec.org/2010/catalog"
             :xml:lang         "en"
             :xmlns:opensearch "http://a9.com/-/spec/opensearch/1.1/"
             }
   :content (concat [{:tag "title" :content ["Books - 11 items"]}]
                    (map entryTagData entries)
                    )
   })


