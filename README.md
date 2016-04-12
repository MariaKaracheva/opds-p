# Opds-p

An [OPDS](http://opds-spec.org/) server that proxies OPDS calls to yandex-disk webdav.


## Usage

**Start server**

    lein ring server

or

    lein ring server-headless

**Build war**

    lein ring uberwar opds-p.war