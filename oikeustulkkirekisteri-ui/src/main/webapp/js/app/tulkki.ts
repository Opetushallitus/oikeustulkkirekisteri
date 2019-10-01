import {Kieli, Kielipari} from "./kielet.ts";
import moment = require("moment");

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
  kutsumanimi?: string;
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
  muokkausviesti?: string
}

const getTulkkiPostData = (tulkki:Tulkki) => {
  let postData:any = angular.copy(tulkki);
  // TODO:is this really necessary? compilcates things. Just load kielet to object map in controller and make method for name?
  postData.kieliParit = _.map(tulkki.kieliparit, (kielipari) => {
    let kielipariAlkaa:Date   = moment(kielipari.voimassaoloAlkaa, 'DD.MM.YYYY').toDate();
    let kielipariPaattyy:Date = moment(kielipari.voimassaoloPaattyy, 'DD.MM.YYYY').toDate();
    return {
      'kielesta': kielipari.kielesta.arvo,
      'kieleen': kielipari.kieleen.arvo,
      'voimassaoloAlkaa': kielipariAlkaa.getTime(),
      'voimassaoloPaattyy': kielipariPaattyy.getTime()
    };
  });
  postData.maakunnat = _.map(tulkki.toimintaAlue, 'arvo');
  return postData;
};

const isTulkkiKutsumanimiValid = (tulkki:Tulkki) => {
  let names = _.split(tulkki.etunimet, ' ');
  const separetedNames = _.flattenDeep(_.invokeMap(names, String.prototype.split, '-'));
  names = _.concat(names, separetedNames);
  const isNameValid = names.indexOf(tulkki.kutsumanimi) !== -1;
  return isNameValid;
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

export {Tulkki, newTulkki, getTulkkiPostData, isTulkkiKutsumanimiValid}