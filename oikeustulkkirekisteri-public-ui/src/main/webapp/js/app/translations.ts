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
  oikeustulkkirekisteri: 'Registret över rättstolkar',
  etsi_oikeustulkkia: 'Sök en rättstolk',
  kuvaus_kaytosta: 'Utbildningsstyrelsen upprätthåller ett register över rättstolkar, där rättstolkar som har godkänts av nämnden för registret över rättstolkar registreras. Med sökmotorn kan du söka en tolk på namn eller enligt språkpar eller verksamhetsområde. I det offentliga registret över rättstolkar finns endast uppgifter om de tolkar som har gett sitt samtycke till att deras uppgifter publiceras på webben.',
  haku_nimen_mukaan: 'Sökning på namn',
  haku_kielipareittain: 'Sökning enligt språkpar',
  kielipari_info: 'Tolken har alltid kompetens att tolka i båda riktningarna, till exempel finska-engelska och engelska-finska.',
  fi_eng: '',
  eng_fi: '',
  haku_maakunnan_mukaan: 'Sökning enligt landskap',
  hae: 'Sök',
  haetaan: 'Söker...',
  ei_tuloksia: 'Sökningen gav inga resultat',
  nimi: 'Namn',
  kieliparit: 'Språkpar',
  toiminta_alue: 'Verksamhetsområde',
  koko_suomi: 'Hela Finland',
  kaikki: 'Alla',
  email: 'E-postadress',
  puhelinnumero: 'Telefonnummer',
  muu_yhteystieto: 'Annan kontaktinformation'
};

angular.module('publicRegistryApp').config(["$translateProvider", ($translateProvider) => {
  $translateProvider.translations('fi', translationFI);
  $translateProvider.translations('sv', translationSV);
  $translateProvider.preferredLanguage('fi');
  $translateProvider.useSanitizeValueStrategy('escape');
}]);

