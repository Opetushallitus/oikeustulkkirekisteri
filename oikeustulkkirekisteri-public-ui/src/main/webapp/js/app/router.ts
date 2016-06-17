console.log('router');
angular.module('publicRegistryApp').config(["$routeProvider", ($routeProvider) => {
  $routeProvider

      .when('/oikeustulkki/:id', {
        templateUrl: 'templates/oikeustulkkiView.html',
        controller: 'oikeustulkkiViewCtrl'
      })

      .otherwise({
        templateUrl: 'templates/oikeustulkkiSearch.html',
        controller: 'oikeustulkkiSearchCtrl'
      });
}]);
