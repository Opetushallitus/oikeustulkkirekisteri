import {Kieli, Kielipari, kielipariMatch} from "../kielet.ts";

angular.module('registryApp').controller('oikeustulkkiSearchCtrl', ["$scope", "Page", "KoodistoService",
    "OikeustulkkiService", '$rootScope', ($scope, Page, KoodistoService, OikeustulkkiService, $rootScope) => {
    Page.setPage('searchOikeustulkki');

    $scope.kieliparit = [];
    $scope.kielesta = null;
    $scope.kieleen = null;
    $scope.kielet = [];
    $scope.results = null;
    $scope.termi = '';
    $scope.maakunnat = [];
    $scope.maakunnatByArvo = {};
    $scope.searching = false;
    $scope.hakuDone = false;

    $scope.removeKielipari = (kielipari:Kielipari) => _.remove($scope.kieliparit, kielipari);

    KoodistoService.getKielet().then(r => {
      $scope.kielet = r.data;
      $scope.kielet.splice(0, 0, {arvo: null, nimi: {FI: 'Kaikki', SV: 'Alla', EN: 'All'}});
      $scope.kielesta = {selected: _.find($scope.kielet, {'arvo': 'FI'})};
      $scope.kieleen = {selected: $scope.kielet[0]};
    });

    KoodistoService.getMaakunnat().then(r => {
      $scope.maakunnat = r.data;
      r.data.map(k => $scope.maakunnatByArvo[k.arvo] = k);
    });

    $scope.tutkinto = {
      erikois : true,
      korkeakoulu : true
    };

    $scope.tutkintoChanged = (selectedTutkinto) => {
      // make sure that one option is always selected
      if (!$scope.tutkinto[selectedTutkinto]) {
        const select = (selectedTutkinto === 'erikois') ? 'korkeakoulu' : 'erikois';
        $scope.tutkinto[select] = true;
      }
    };

    $scope.switchToPreviousPage = () => {
      if ($scope.pagination.current > 1) {
        $scope.pagination.current -= 1;
      }
    };

    $scope.switchToNextPage = () => {
      if ($scope.pagination.current < $scope.pagination.pages.length) {
        $scope.pagination.current += 1;
      }
    };

    $scope.search = () => {
      let tutkintoTyyppi = $scope.tutkinto.erikois && !$scope.tutkinto.korkeakoulu ? 'OIKEUSTULKIN_ERIKOISAMMATTITUTKINTO' : null;
      tutkintoTyyppi = !$scope.tutkinto.erikois && $scope.tutkinto.korkeakoulu ? 'MUU_KORKEAKOULUTUTKINTO' : tutkintoTyyppi;
      $scope.searching = true;
      OikeustulkkiService.getTulkit($scope.termi, $scope.kieliparit, tutkintoTyyppi).then((results)=> {
        $scope.searching = false;
        $scope.hakuDone = true;
        $scope.pagination = {
          pages: _.chunk(results.data, 10),
          current: 1
        };
      }, (e) => {
        $rootScope.$broadcast('addError', $(".translations [tt='oikeustulkki_search_failed']").text());
        $scope.searching = false;
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

    $scope.hasExpired = (date) => new Date(date[0], date[1]-1, date[2]) < new Date();
  }]);
