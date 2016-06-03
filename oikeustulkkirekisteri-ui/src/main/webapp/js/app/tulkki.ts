class Maakunta{
  arvo: string;
  uri: string;
  nimi: any;
}

class Tulkki{
  etunimi: string;
  sukunimi: string;
  hetu: string;
  katuosoite: string;
  postinumero: number;
  postitoimipaikka: string;
  sahkopostiosoite: string;
  puhelinnumero: string;
  muuYhteystieto: string;
  tutkinto: string;
  kieliparit: Kielipari[];
  lisatietoa: string;
  toimintaAlue: Maakunta[];
  julkaisulupa: boolean;

  constructor(){
    this.kieliparit = [];
  }
}

class Kielipari {
  kielesta:string;
  kieleen:string;

  constructor(kielesta:string, kieleen:string) {
    this.kielesta = kielesta;
    this.kieleen = kieleen;
  }
}

angular.module('registryApp').service('KoodistoService', ($resource) => {

  const koodisto = $resource('/oikeustulkkirekisteri-service/api/koodisto/:koodi', {koodi:'@koodi'}, {
    list: {method: 'GET', isArray: true}
  });

  const getMaakunnat = (): Maakunta[] => koodisto.list({koodi: 'maakunnat'});
  const getKunnat = () => koodisto.list({koodi: 'kunnat'});

  return {
    getMaakunnat: getMaakunnat,
    getKunnat: getKunnat
  };

});

angular.module('registryApp').controller('translatorCreateCtrl', ($scope, Page, KoodistoService) => {
  Page.setPage('addTranslator');

  $scope.tulkki = new Tulkki();
  $scope.regions = KoodistoService.getMaakunnat();
  $scope.tulkki.toimintaAlue = $scope.regions[0];
  $scope.kielesta;
  $scope.kieleen;

  $scope.addKielipari = () => {
    $scope.tulkki.kieliparit.push(new Kielipari($scope.kielesta, $scope.kieleen));
  };

  $scope.save = () => {
    console.log('save', $scope.tulkki);
  };

});
