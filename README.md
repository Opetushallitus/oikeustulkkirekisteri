# Oikeustulkkirekisteri

## Tarkoitus

Oikeustulkkirekisteri on Opetushallituksen ylläpitämä rekisteri, jonka tarkoitus on auttaa ihmisiä löytämään tulkki, 
joka on riittävän pätevä toimimaan tulkkina oikeudellisissa asioissa.

Lisätietoja [Opetushallituksen sivuilta](http://oph.fi/koulutus_ja_tutkinnot/oikeustulkkirekisteri).

## Teknologiat

### Palvelin
* Tomcat 7
* Java 8
* Spring 4
* JPA / Hibernate 4
* PostgreSQL

### UI
* Angular JS
* Bootstrap

### Käännösautomaatio
* Maven 3
* NPM
* Webpack

## Käyttöönotto

Varmista, että asennettuna on:

* [Java JDK](http://www.oracle.com/technetwork/java/javase/downloads/index.html) versio >= 8
* [Maven](https://maven.apache.org/download.cgi) versio >= 3.1
* [PostgreSQL-palvelin](https://www.postgresql.org/download/) versio >= 9.1

### Luo paikallinen PostgreSQL-tietokanta
    echo "CREATE USER oikeustulkkirekisteri; CREATE DATABASE oikeustulkkirekisteri OWNER oikeustulkkirekisteri;" | psql -hlocalhost -Upostgres

ja salli siihen yhteydet ([pg_hba.conf](https://www.postgresql.org/docs/9.1/static/auth-pg-hba-conf.html)).

## Testien ajaminen

    mvn clean test
    
## Käynnistäminen

    mvn tomcat7-run
    
## Kääntäminen

    mvn clean install -Pprod
