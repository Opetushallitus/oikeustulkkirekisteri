import {Kieli, Kielipari} from "./kielet.ts";

class Maakunta{
  arvo: string;
  uri: string;
  nimi: any;
}

class Osoite{
  katuosoite: string;
  postinumero: string;
  postitoimipaikka: string;
}

class Tulkki{
  etunimet: string;
  sukunimi: string;
  hetu: string;
  osoite: Osoite;
  osoiteJulkaisulupa: boolean;
  email: string;
  sahkopostiJulkaisulupa: boolean;
  puhelinnumero: string;
  puhelinnumeroJulkaisulupa: boolean;
  muuYhteystieto: string;
  muuYhteystietoJulkaisulupa: boolean;
  tutkintoTyyppi: string;
  kieliparit: Kielipari[];
  lisatietoa: string;
  kokoSuomi: boolean;
  toimintaAlue: Maakunta[];
  julkaisulupa: boolean;
  alkaa: string;

  constructor(){
    this.kieliparit = [];
    this.osoiteJulkaisulupa = true;
    this.sahkopostiJulkaisulupa = true;
    this.puhelinnumeroJulkaisulupa = false;
    this.muuYhteystietoJulkaisulupa = false;
    this.kokoSuomi = true;
    this.tutkintoTyyppi = 'OIKEUSTULKIN_ERIKOISAMMATTITUTKINTO';
  }
  
  getTulkkiPostData(){
    let postData:any = this;
    postData.kieliParit = _.map(this.kieliparit, (kielipari) => {
      return {'kielesta': kielipari.kielesta.arvo, 'kieleen': kielipari.kieleen.arvo};
    });
    postData.alkaa = new Date().getTime();
    return postData;
  }
}

export {Tulkki}