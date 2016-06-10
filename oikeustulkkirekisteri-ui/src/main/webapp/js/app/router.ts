angular.module('registryApp').config(["$routeProvider", ($routeProvider) => {
  $routeProvider

      .when('/addOikeustulkki', {
        templateUrl: 'templates/oikeustulkkiCreate.html',
        controller: 'oikeustulkkiCreateCtrl'
      })

      .when('/oikeustulkki/:id', {
        templateUrl: 'templates/oikeustulkkiView.html',
        controller: 'oikeustulkkiViewCtrl'
      })

      .when('/oikeustulkki/:id/:action', {
        templateUrl: 'templates/oikeustulkkiEdit.html',
        controller: 'oikeustulkkiEditCtrl'
      })

      .otherwise({
          templateUrl: 'templates/oikeustulkkiSearch.html',
          controller: 'oikeustulkkiSearchCtrl'
      });
}]);
