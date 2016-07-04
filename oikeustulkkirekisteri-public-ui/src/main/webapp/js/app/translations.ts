const translationFI = {
  toinen_kieli: 'På svenska',
  oikeustulkkirekisteri: 'Oikeustulkkirekisteri',
  etsi_oikeustulkkia: 'Etsi oikeustulkkia',
  kuvaus_kaytosta: 'Opetushallitus ylläpitää oikeustulkkirekisteriä, johon oikeustulkkirekisterilautakunnan hyväksymät oikeustulkit merkitään. Hakukoneella voit hakea tulkkia joko nimen, kieliparin tai toiminta-alueen mukaan. Julkinen oikeustulkkirekisteri sisältää tiedot vain niistä tulkeista, jotka ovat antaneet luvan tietojensa julkaisemiseen verkossa.',
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
  oikeustulkkirekisteri: 'Oikeustulkkirekisteri',
  etsi_oikeustulkkia: 'Etsi oikeustulkkia',
  kuvaus_kaytosta: 'Ruotsinkielen lokalisoinnit tulossa.',
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
  kaikki: 'Alla',
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

