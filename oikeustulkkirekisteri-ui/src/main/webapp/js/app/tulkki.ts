import {Kieli, Kielipari} from "./kielet.ts";

interface Maakunta{
  arvo: string;
  uri: string;
  nimi: any;
}

interface Osoite{
  katuosoite: string;
  postinumero: string;
  postitoimipaikka: string;
}

interface Tulkki{
  id?:number;
  etunimet?: string;
  sukunimi?: string;
  hetu?: string;
  osoite?: Osoite;
  osoiteJulkaisulupa?: boolean;
  email?: string;
  julkaisulupaEmail?: boolean;
  puhelinnumero?: string;
  julkaisulupaPuhelinnumero?: boolean;
  muuYhteystieto?: string;
  julkaisulupaMuuYhteystieto?: boolean;
  tutkintoTyyppi?: string;
  kieliparit?: Kielipari[];
  lisatiedot?: string;
  kokoSuomi?: boolean;
  maakunnat?: string[];
  toimintaAlue?: Maakunta[];
  julkaisulupa?: boolean;
  alkaa?: Date;
  paattyy?: Date;
  muokkausviesti?: string
}

const getTulkkiPostData = (tulkki:Tulkki) => {
  let postData:any = angular.copy(tulkki);
  // TODO:is this really necessary? compilcates things. Just load kielet to object map in controller and make method for name?
  postData.kieliParit = _.map(tulkki.kieliparit, (kielipari) => {
    return {'kielesta': kielipari.kielesta.arvo, 'kieleen': kielipari.kieleen.arvo};
  });
  postData.alkaa = tulkki.alkaa.getTime();
  if (tulkki.paattyy) {
    postData.paattyy = tulkki.paattyy.getTime();
  }
  postData.maakunnat = _.map(tulkki.toimintaAlue, 'arvo');
  return postData;
};

function newTulkki():Tulkki {
  return {
    kieliparit: [],
    osoiteJulkaisulupa: true,
    julkaisulupaEmail: true,
    julkaisulupaPuhelinnumero: false,
    julkaisulupaMuuYhteystieto: false,
    kokoSuomi: true,
    toimintaAlue: [],
    tutkintoTyyppi: 'OIKEUSTULKIN_ERIKOISAMMATTITUTKINTO'
  };
}

export {Tulkki, newTulkki, getTulkkiPostData}