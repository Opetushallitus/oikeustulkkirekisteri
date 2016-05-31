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

### oikeustulkkirekisteri-service

    mvn tomcat7-run
   
Swagger-dokumentaation tulisi löytyä http://localhost:8080/oikeustulkkirekisteri-service/
   
### oikeustulkkirekisteri-ui

Kääntäminen ja tarvittavien käännöstyökalujen (node, npm ja riippuvuudet) lataus.

    mvn install 

Ajaminen webpack-dev-server:llä (ajaa tarvittaessa mvn install:n ensin):

    ./dev.sh
    
(Tai aja node dev.js src/main/webapp-hakemistossa)

Nyt löydät bunldatun UI:n osoitteesta http://localhost:9000/ ja muutokset päivittyvät sinne. Polku /oikeustulkkirekisteri-service proxytään Tomcatille.
Voit myös käyttää polusta http://localhost:9000/webpack-dev-server/ löytyvää iframe-versiota, joka päivittyy selaimessa automaattisesti.

## Kääntäminen

    mvn clean install
