import {Kieli, Kielipari, kielipariMatch} from "../kielet.ts";

angular.module('publicRegistryApp').controller('oikeustulkkiSearchCtrl', ["$scope", "OikeustulkkiService",
  "KoodistoService", "$translate", ($scope, OikeustulkkiService, KoodistoService, $translate) => {
    $scope.termi = '';
    $scope.maakunnatByArvo = {};
    $scope.kieliparit = [];
    $scope.kielesta = null;
    $scope.kieleen = null;
    $scope.kielet = [];
    $scope.regions = [];
    $scope.toimintaAlue = {value: {}};
    $scope.searching = false;

    let kokoSuomi = {
      "arvo": "00",
      "nimi": {}
    };
    
    let kaikkiKielet = {
      "arvo": null,
      "nimi": {}
    };

    $translate(['koko_suomi', 'kaikki']).then((fi) => {
      kokoSuomi.nimi['FI'] = fi.koko_suomi;
      kaikkiKielet.nimi['FI'] = fi.koko_suomi;
    });

    KoodistoService.getMaakunnat().then(r => {
      $scope.regions = _.concat(kokoSuomi, r.data);
      $scope.toimintaAlue = {value: $scope.regions[0]};
      r.data.map(k => $scope.maakunnatByArvo[k.arvo] = k);
    });

    KoodistoService.getKielet().then(r => {
      $scope.kielet = _.concat(kaikkiKielet, r.data);
      $scope.kielesta = {selected: _.find($scope.kielet, {'arvo': 'FI'})};
      // $scope.kielesta = {selected: $scope.kielet[0]};
      $scope.kieleen = {selected: $scope.kielet[0]};
    });

    $scope.getKieliNimi = (arvo:string) => {
      const lang = $translate.proposedLanguage().toUpperCase();
      return _.find($scope.kielet, {'arvo': arvo}).nimi[lang];
    };

    $scope.maakunta = (koodiArvo:string) => {
      if ($scope.maakunnatByArvo[koodiArvo]) {
        const lang = $translate.proposedLanguage().toUpperCase();
        return $scope.maakunnatByArvo[koodiArvo].nimi[lang];
      }
      return '';
    };

    $scope.search = () => {
      $scope.searching = true;
      $scope.tulkit = [];

      const kieliparit = [{
        'kielesta': $scope.kielesta.selected.arvo,
        'kieleen': $scope.kieleen.selected.arvo
      }];
      OikeustulkkiService.getTulkit($scope.termi, kieliparit, $scope.toimintaAlue.value.arvo).then((r) => {
        $scope.tulkit = r.data;
      }).finally(()=> {
        $scope.searching = false;
      });
    };

    $scope.toggleTulkkiInfo = (tulkki) => {
      if (tulkki.detailsFetched) {
        tulkki.visible = !tulkki.visible;
      } else {
        OikeustulkkiService.getTulkki(tulkki.id).then((res) => {
          tulkki.email = res.data.email;
          tulkki.puhelinnumero = res.data.puhelinnumero;
          tulkki.muuYhteystieto = res.data.muuYhteystieto;
          tulkki.visible = true;
          tulkki.detailsFetched = true;
        });
      }
    }

  }]);
