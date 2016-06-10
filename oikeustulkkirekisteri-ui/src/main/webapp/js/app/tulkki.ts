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
  julkaisulupaEmail: boolean;
  puhelinnumero: string;
  julkaisulupaPuhelinnumero: boolean;
  muuYhteystieto: string;inde
  julkaisulupaMuuYhteystieto: boolean;
  tutkintoTyyppi: string;
  kieliparit: Kielipari[];
  lisatiedot: string;
  kokoSuomi: boolean;
  maakunnat: string[];
  toimintaAlue: Maakunta[];
  julkaisulupa: boolean;
  alkaa: Date;

  constructor(){
    this.kieliparit = [];
    this.osoiteJulkaisulupa = true;
    this.julkaisulupaEmail = true;
    this.julkaisulupaPuhelinnumero = false;
    this.julkaisulupaMuuYhteystieto = false;
    this.kokoSuomi = true;
    this.tutkintoTyyppi = 'OIKEUSTULKIN_ERIKOISAMMATTITUTKINTO';
  }
  
  getTulkkiPostData(){
    let postData:any = angular.copy(this);
    postData.kieliParit = _.map(this.kieliparit, (kielipari) => {
      return {'kielesta': kielipari.kielesta.arvo, 'kieleen': kielipari.kieleen.arvo};
    });
    postData.alkaa = this.alkaa.getTime();
    this.maakunnat = _.map(this.toimintaAlue, 'arvo');
    return postData;
  }
}

export {Tulkki}