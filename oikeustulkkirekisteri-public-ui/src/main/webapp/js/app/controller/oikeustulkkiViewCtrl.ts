angular.module('publicRegistryApp').controller('oikeustulkkiViewCtrl', ["$scope", "OikeustulkkiService",
  "$routeParams", ($scope, OikeustulkkiService, $routeParams) => {
    OikeustulkkiService.getTulkki($routeParams.id).then((results) => {
      $scope.tulkki = results.data;
    });
  }]);
