angular.module('publicRegistryApp').config(["$routeProvider", ($routeProvider) => {
  $routeProvider
      .otherwise({
        templateUrl: 'templates/oikeustulkkiSearch.html',
        controller: 'oikeustulkkiSearchCtrl'
      });
}]);
