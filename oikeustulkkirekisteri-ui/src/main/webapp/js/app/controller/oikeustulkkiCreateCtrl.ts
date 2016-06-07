import {Kieli, Kielipari} from "../kielet.ts";

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
  osoiteJulkaisulupa: boolean;
  sahkopostiosoite: string;
  sahkopostiJulkaisulupa: boolean;
  puhelinnumero: string;
  puhelinnumeroJulkaisulupa: boolean;
  muuYhteystieto: string;
  muuYhteystietoJulkaisulupa: boolean;
  tutkinto: string;
  kieliparit: Kielipari[];
  lisatietoa: string;
  kokoSuomi: boolean;
  toimintaAlue: Maakunta[];
  julkaisulupa: boolean;

  constructor(){
    this.kieliparit = [];
    this.osoiteJulkaisulupa = true;
    this.sahkopostiJulkaisulupa = true;
    this.puhelinnumeroJulkaisulupa = false;
    this.muuYhteystietoJulkaisulupa = false;
    this.kokoSuomi = true;
    this.tutkinto = 'ERIKOISAMMATTITUTKINTO';
  }

}



angular.module('registryApp').controller('oikeustulkkiCreateCtrl', ($scope, Page, KoodistoService) => {
  Page.setPage('addOikeustulkki');

  $scope.kieliparit = [];
  $scope.showErrors = false;
  $scope.kielesta;
  $scope.kieleen;

  KoodistoService.getKielet().then(r => {
    $scope.kielet = r.data;
    $scope.kielesta = {selected: $scope.kielet[0]};
    $scope.kieleen = {selected: $scope.kielet[1]};
  });

  $scope.tulkki = new Tulkki();
  $scope.regions = [];

  KoodistoService.getMaakunnat().then(r => {
    const regionsWithoutUnknownOption = _.filter(r.data, (region) => {
      return region.arvo !== "99";
    });
    $scope.regions = regionsWithoutUnknownOption;
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
    if (!_.isEmpty($scope.tulkkiForm.$error)) {
      $scope.showErrors = true;
    }
    console.log('save', $scope.tulkki);
  };

  $scope.addAllRegions = () => {
    $scope.tulkki.toimintaAlue = $scope.regions;
  };

});
