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

### Luo kotihakemistoon security-configurtaatio

- Luo kotihakemistoosi hakemisto oph-configuration, jonka sisälle tiedosto security-context-backend.xml.
- Lisää sisällöksi esimerkiksi tyhjä Springin security-konfiguraatio:

        <beans:beans xmlns="http://www.springframework.org/schema/security"
                  xmlns:beans="http://www.springframework.org/schema/beans"
                  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                  xmlns:p="http://www.springframework.org/schema/p"
                  xsi:schemaLocation="http://www.springframework.org/schema/beans
                         http://www.springframework.org/schema/beans/spring-beans-4.1.xsd
                         http://www.springframework.org/schema/security http://www.springframework.org/schema/security/spring-security-3.2.xsd">      
        </beans:beans>
                    

## Testien ajaminen

    mvn clean test
    
## Käynnistäminen

### oikeustulkkirekisteri-service

Rekisterin backend-palvelu ajetana Tomcatilla:

    mvn tomcat7-run
   
Swagger-dokumentaation tulisi löytyä [http://localhost:8080/oikeustulkkirekisteri-service/](http://localhost:8080/oikeustulkkirekisteri-service/)

### oikeustulkkirekisteri-ui

Rekisterin hallintakäyttöliittymä. Kääntäminen ja tarvittavien käännöstyökalujen (node, npm ja riippuvuudet) lataus. ja paketointi:

    mvn install 

Ajaminen webpack-dev-server:llä (ajaa tarvittaessa mvn install:n ensin automaattisesti):

    ./dev.sh

(Tai vaihtoehtoisesti aja src/main/webapp-hakemistossa node dev.js ja eri konsolissa webpack --watch)

Nyt löydät bunldatun UI:n osoitteesta [http://localhost:9000/](http://localhost:9000/) ja muutokset päivittyvät sinne. Polku /oikeustulkkirekisteri-service proxytään Tomcatille.
Voit myös käyttää polusta [http://localhost:9000/webpack-dev-server/](http://localhost:9000/webpack-dev-server/) löytyvää iframe-versiota, joka päivittyy selaimessa automaattisesti.

### oikeustulkkirekisteri-public-ui

Rekisterin julkinen käyttöliittymä. Kääntäminen ja ajaminen samoin kuin oikeustulkkirekisteri-ui mutta porttina 9010:

[http://localhost:9000/](http://localhost:9010/) ja [http://localhost:9000/webpack-dev-server/](http://localhost:9010/webpack-dev-server/)

## Kääntäminen

Deployta varten:

    mvn clean install
