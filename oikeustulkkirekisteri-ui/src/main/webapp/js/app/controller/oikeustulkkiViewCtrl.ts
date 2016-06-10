angular.module('registryApp').controller('oikeustulkkiViewCtrl', ["$scope", "Page", "$location",
  "OikeustulkkiService", "$window", "$routeParams", "KoodistoService", ($scope, Page, $location, OikeustulkkiService, $window, $routeParams, KoodistoService) => {
    Page.setPage('viewOikeustulkki');

    $scope.showRemoveDialog = false;
    $scope.showErrorDialog = false;
    $scope.kielet = {};
    $scope.maakunnat = {};

    OikeustulkkiService.getTulkki($routeParams.id).then((results) => {
      $scope.tulkki = results.data;
    });

    KoodistoService.getKielet().then(r => {
      r.data.map(k => $scope.kielet[k.arvo] = k);
    });
    KoodistoService.getMaakunnat().then(r => {
      r.data.map(k => $scope.maakunnat[k.arvo] = k);
    });

    $scope.history = [{
      muokattuPvm: '1.6.2014',
      kommentti: 'Uusittu voimassaolo',
      visible: false
    }, {
      muokattuPvm: '1.11.2015',
      kommentti: 'Lisätty kielipari',
      visible: false
    }];

    $scope.hideDialog = () => {
      $scope.showRemoveDialog = false;
    };

    $scope.showItem = (item) => {
      item.visible = !item.visible;
    };

    $scope.hideErrorDialog = () => {
      $scope.showErrorDialog = false;
    };
    
    $scope.kieli = (koodiArvo:string) => {
      if ($scope.kielet[koodiArvo]) {
        return $scope.kielet[koodiArvo].nimi['FI'] || $scope.kielet[koodiArvo].nimi['SV'];
      }
      return '';
    };
    $scope.maakunta = (koodiArvo:string) => {
      if ($scope.maakunnat[koodiArvo]) {
        return $scope.maakunnat[koodiArvo].nimi['FI'] || $scope.maakunnat[koodiArvo].nimi['SV'];
      }
      return '';
    };

    $scope.remove = () => {
      OikeustulkkiService.removeTulkki($routeParams.id).then(()=> {
        $window.location.href = "#";
      }, ()=> {
        $scope.showErrorDialog = true;
      });
      $scope.showRemoveDialog = false;
    };

  }]);