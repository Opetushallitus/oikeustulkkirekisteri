angular.module('registryApp').controller('oikeustulkkiViewCtrl', ["$scope", "Page", "$location",
  "OikeustulkkiService", "$window", "$routeParams", "KoodistoService", "$rootScope", ($scope, Page, $location, OikeustulkkiService, $window, $routeParams, KoodistoService, $rootScope) => {
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
        $rootScope.$broadcast('addSuccess', $(".translations [tt='oikeustulkki_deleted']").text());
        $window.location.href = "#";
      }, ()=> {
        $scope.showErrorDialog = true;
      });
      $scope.showRemoveDialog = false;
    };

  }]);