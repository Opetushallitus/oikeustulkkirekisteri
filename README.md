# Oikeustulkkirekisteri

## Tarkoitus

Oikeustulkkirekisteri on Opetushallituksen ylläpitämä rekisteri, jonka tarkoitus on auttaa ihmisiä löytämään tulkki, 
joka on riittävän pätevä toimimaan tulkkina oikeudellisissa asioissa.

Lisätietoja Opetushallituksen sivuilta: http://oph.fi/koulutus_ja_tutkinnot/oikeustulkkirekisteri

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

TODO

## Testien ajaminen

    mvn clean test
    
## Käynnistäminen

    mvn tomcat7-run
    
## Kääntäminen

    mvn clean install -Pprod
