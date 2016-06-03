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
  kielesta:Kieli;
  kieleen:Kieli;

  isMatch(kielipari:Kielipari){
    return this.kieleen === kielipari.kieleen && this.kielesta === kielipari.kielesta;
  }

  constructor(kielesta:Kieli, kieleen:Kieli) {
    if (kielesta === kieleen) {
      console.error("kielet ovat samat");
      //TODO virheiden näyttäminen käyttäjälle
      throw "kielet ovat samat";
    }

    this.kielesta = kielesta;
    this.kieleen = kieleen;
  }
}

angular.module('registryApp').controller('oikeustulkkiCreateCtrl', ($scope, Page, KoodistoService) => {
  Page.setPage('addOikeustulkki');

  $scope.kieliparit = [];
  $scope.kielesta;
  $scope.kieleen;

  KoodistoService.getKielet().then(r => {
    $scope.kielet = r.data;
    $scope.kielesta = {selected: $scope.kielet[0]};
    $scope.kieleen = {selected: $scope.kielet[0]};
  });

  $scope.tulkki = new Tulkki();
  $scope.regions = [];

  KoodistoService.getMaakunnat().then(r => {
    $scope.regions = r.data;
  });

  $scope.addKielipari = () => {
    const kielipari:Kielipari = new Kielipari($scope.kielesta.selected, $scope.kieleen.selected);

    var kielipariAlreadyExists = _.some($scope.tulkki.kieliparit, (kpari) => {
      return kielipari.isMatch(kpari);
    });

    if(kielipariAlreadyExists){
      console.error("kielipari jo lisätty");
      return;
    }

    $scope.tulkki.kieliparit.push(kielipari);
  };

  $scope.removeKielipari = (kielipari:Kielipari) => _.remove($scope.tulkki.kieliparit, kielipari);

  $scope.save = () => {
    console.log('save', $scope.tulkki);
  };


});
