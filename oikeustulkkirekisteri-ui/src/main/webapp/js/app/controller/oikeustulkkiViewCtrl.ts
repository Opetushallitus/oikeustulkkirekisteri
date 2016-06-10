angular.module('registryApp').controller('oikeustulkkiViewCtrl', ["$scope", "Page", "$location",
  "OikeustulkkiService", "$window", "$routeParams", ($scope, Page, $location, OikeustulkkiService, $window, $routeParams) => {
    Page.setPage('viewOikeustulkki');

    $scope.showRemoveDialog = false;
    $scope.showErrorDialog = false;

    OikeustulkkiService.getTulkki($routeParams.id).then((results) => {
      $scope.tulkki = results.data;
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

    $scope.remove = () => {
      OikeustulkkiService.removeTulkki($routeParams.id).then(()=> {
        $window.location.href = "#search";
      }, ()=> {
        $scope.showErrorDialog = true;
      });

      $scope.showRemoveDialog = false;
    };

  }]);