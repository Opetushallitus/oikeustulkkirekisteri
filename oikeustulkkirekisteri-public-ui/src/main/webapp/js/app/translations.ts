const translationFI = {
  toinen_kieli: 'På svenska',
  oikeustulkkirekisteri: 'Oikeustulkkirekisteri',
  etsi_oikeustulkkia: 'Etsi oikeustulkkia',
  kuvaus_kaytosta: 'TODO: kuvaus käytöstä. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam tempor orci in sapien pellentesque, nec pretium lorem pulvinar. Nulla eget imperdiet ante. Nullam ullamcorper tempus neque, id porta augue malesuada nec. Nulla facilisi. Phasellus dui orci, facilisis ut quam eget, viverra dictum mauris. Nunc eu vestibulum mauris, tincidunt venenatis purus. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. In eget convallis orci.',
  haku_nimen_mukaan: 'Haku nimen mukaan',
  haku_kielipareittain: 'Haku kielipareittain',
  kielipari_info: 'Molemman suuntainen pätevyys esim.',
  fi_eng: 'suomi <-> englanti',
  eng_fi: 'englanti <-> suomi',
  haku_maakunnan_mukaan: 'Haku maakunnan mukaan',
  hae: 'Hae',
  haetaan: 'Haetaan...',
  ei_tuloksia: 'Haulla ei löytynyt tuloksia',
  nimi: 'Nimi',
  kieliparit: 'Kieliparit',
  toiminta_alue: 'Toiminta-alue',
  koko_suomi: 'Koko Suomi',
  kaikki: 'Kaikki',
  email: 'Sähköpostiosoite',
  puhelinnumero: 'Puhelinnumero',
  muu_yhteystieto: 'Muu yhteystieto'
};

const translationSV = {
  toinen_kieli: 'Suomeksi',

  oikeustulkkirekisteri: 'Oikeustulkkirekisteri - sv',
  etsi_oikeustulkkia: 'Etsi oikeustulkkia',
  kuvaus_kaytosta: 'TODO: kuvaus käytöstä. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam tempor orci in sapien pellentesque, nec pretium lorem pulvinar. Nulla eget imperdiet ante. Nullam ullamcorper tempus neque, id porta augue malesuada nec. Nulla facilisi. Phasellus dui orci, facilisis ut quam eget, viverra dictum mauris. Nunc eu vestibulum mauris, tincidunt venenatis purus. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. In eget convallis orci.',
  haku_nimen_mukaan: 'Haku nimen mukaan',
  haku_kielipareittain: 'Haku kielipareittain',
  kielipari_info: 'Molemman suuntainen pätevyys esim.',
  fi_eng: 'suomi <-> englanti',
  eng_fi: 'englanti <-> suomi',
  haku_maakunnan_mukaan: 'Haku maakunnan mukaan',
  hae: 'Hae',
  haetaan: 'Haetaan...',
  ei_tuloksia: 'Haulla ei löytynyt tuloksia',
  nimi: 'Nimi',
  kieliparit: 'Kieliparit',
  toiminta_alue: 'Toiminta-alue',
  koko_suomi: 'Koko Suomi -sv',
  kaikki: 'Kaikki - sv',
  email: 'Sähköpostiosoite',
  puhelinnumero: 'Puhelinnumero',
  muu_yhteystieto: 'Muu yhteystieto'
};

angular.module('publicRegistryApp').config(["$translateProvider", ($translateProvider) => {
  $translateProvider.translations('fi', translationFI);
  $translateProvider.translations('sv', translationSV);
  $translateProvider.preferredLanguage('fi');
  $translateProvider.useSanitizeValueStrategy('escape');
}]);

