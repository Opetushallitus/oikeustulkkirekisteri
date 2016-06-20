import {Kieli, Kielipari, kielipariMatch} from "../kielet.ts";

angular.module('publicRegistryApp').controller('oikeustulkkiSearchCtrl', ["$scope", "OikeustulkkiService",
  "KoodistoService", ($scope, OikeustulkkiService, KoodistoService) => {
    $scope.termi = '';
    $scope.maakunnatByArvo = {};
    $scope.kieliparit = [];
    $scope.kielesta = null;
    $scope.kieleen = null;
    $scope.kielet = [];
    $scope.regions = [];
    $scope.toimintaAlue = {value: {}};
    $scope.tulkkiInfoVisible = [];

    //TODO localize
    const kokoSuomi = {
      "arvo": "00",
      "nimi": {
        "FI": "Koko Suomi",
        "SV": ""
      }
    };

    const kaikkiKielet = {
      "arvo": "",
      "nimi": {
        "SV": "",
        "FI": "Kaikki",
        "EN": ""
      }
    };

    KoodistoService.getMaakunnat().then(r => {
      $scope.regions = _.concat(kokoSuomi, r.data);
      $scope.toimintaAlue = {value: $scope.regions[0]};
    });

    KoodistoService.getKielet().then(r => {
      $scope.kielet = _.concat(kaikkiKielet, r.data);
      // $scope.kielesta = {selected: _.find($scope.kielet, {'arvo': 'FI'})};
      $scope.kielesta = {selected: $scope.kielet[0]};
      $scope.kieleen = {selected: $scope.kielet[0]};
    });

    $scope.getKieliNimi = (arvo:string) => {
      return _.find($scope.kielet, {'arvo': arvo}).nimi.FI;
    };

    $scope.maakunta = (koodiArvo:string) => {
      if ($scope.maakunnatByArvo[koodiArvo]) {
        return $scope.maakunnatByArvo[koodiArvo].nimi['FI'] || $scope.maakunnat[koodiArvo].nimi['SV'];
      }
      return '';
    };

    $scope.search = () => {
      // const kieliparit = _.map($scope.kieliparit, (kielipari) => {
      //   return {'kielesta': kielipari.kielesta.arvo, 'kieleen': kielipari.kieleen.arvo}
      // });

      console.log('post', $scope);

      const kieliparit = [{
        'kielesta': $scope.kielesta.selected.arvo,
        'kieleen': $scope.kieleen.selected.arvo
      }];
      OikeustulkkiService.getTulkit($scope.termi, kieliparit, $scope.toimintaAlue.value.arvo).then((r) => {
        $scope.tulkit = r.data;
      });
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

    $scope.removeKielipari = (kielipari:Kielipari) => _.remove($scope.kieliparit, kielipari);

    $scope.showTulkkiInfo = (tulkkiId) => {
      $scope.tulkkiInfoVisible.push(tulkkiId);
      OikeustulkkiService.getTulkki(tulkkiId).then((res) => {
        console.log('res', res);
      });
    }

  }]);
