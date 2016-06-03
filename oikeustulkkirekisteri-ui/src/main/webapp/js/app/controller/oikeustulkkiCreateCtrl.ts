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

class Kieli{
  arvo: string;
  nimi: any;
  uri: string;
}

class Kielipari {
  kielesta:string;
  kieleen:string;

  constructor(kielesta:string, kieleen:string) {
    this.kielesta = kielesta;
    this.kieleen = kieleen;
  }
}

angular.module('registryApp').controller('oikeustulkkiCreateCtrl', ($scope, Page, KoodistoService) => {
  Page.setPage('addOikeustulkki');

  console.log("lo", _.capitalize('test'));
  $scope.kielesta;
  $scope.kieleen;
  $scope.kieliparit = [];

  KoodistoService.getKielet().then(r => {
    $scope.kielet = r.data;
    console.log('kielet', $scope.kielet);
  });

  $scope.tulkki = new Tulkki();
  $scope.regions = [];

  KoodistoService.getMaakunnat().then(r => {
    $scope.regions = r.data;
  });

  $scope.tulkki.toimintaAlue = $scope.regions[0];

  $scope.addKielipari = () => {
    console.log('kielesta', $scope.kielesta);
    $scope.tulkki.kieliparit.push(new Kielipari($scope.kielesta, $scope.kieleen));
  };

  $scope.save = () => {
    console.log('save', $scope.tulkki);
  };


});
