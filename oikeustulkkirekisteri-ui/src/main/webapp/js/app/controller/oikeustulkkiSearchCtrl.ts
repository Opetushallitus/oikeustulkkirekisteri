import {Kieli, Kielipari, kielipariMatch} from "../kielet.ts";

angular.module('registryApp').controller('oikeustulkkiSearchCtrl', ["$scope", "Page", "KoodistoService",
  "OikeustulkkiService", ($scope, Page, KoodistoService, OikeustulkkiService) => {

    Page.setPage('searchOikeustulkki');

    $scope.kieliparit = [];
    $scope.kielesta = null;
    $scope.kieleen = null;
    $scope.kielet = [];
    $scope.results = null;
    $scope.termi = '';
    $scope.maakunnat = [];
    $scope.maakunnatByArvo = {};

    $scope.removeKielipari = (kielipari:Kielipari) => _.remove($scope.kieliparit, kielipari);

    KoodistoService.getKielet().then(r => {
      $scope.kielet = r.data;
      $scope.kielesta = {selected: _.find($scope.kielet, {'arvo': 'FI'})};
      $scope.kieleen = {selected: $scope.kielet[1]};
    });

    KoodistoService.getMaakunnat().then(r => {
      $scope.maakunnat = r.data;
      r.data.map(k => $scope.maakunnatByArvo[k.arvo] = k);
    });

    $scope.search = () => {
      OikeustulkkiService.getTulkit($scope.termi, $scope.kieliparit).then((results)=> {
        $scope.results = results.data;
      });
    };

    $scope.getKieliNimi = (arvo:string) => {
      return _.find($scope.kielet, {'arvo': arvo}).nimi.FI;
    };

    $scope.maakunta = (koodiArvo:string) => {
      if ($scope.maakunnatByArvo[koodiArvo]) {
        return $scope.maakunnatByArvo[koodiArvo].nimi['FI'] || $scope.maakunnat[koodiArvo].nimi['SV'];
      }
      return '';
    };

    $scope.addKielipari = () => {
      const kielipari:Kielipari = {kielesta: $scope.kielesta.selected, kieleen: $scope.kieleen.selected};

      var kielipariAlreadyExists = _.some($scope.kieliparit, (kpari) => {
        return kielipariMatch(kielipari, kpari);
      });

      if (kielipariAlreadyExists) {
        console.error("kielipari jo lisätty");
        return;
      }

      $scope.kieliparit.push(kielipari);
    };

  }]);
