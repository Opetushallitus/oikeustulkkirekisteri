angular.module('registryApp').config(["$routeProvider", ($routeProvider) => {
  $routeProvider

      .when('/search', {
        templateUrl: 'templates/oikeustulkkiSearch.html',
        controller: 'oikeustulkkiSearchCtrl'
      })

      .when('/addOikeustulkki', {
        templateUrl: 'templates/oikeustulkkiCreate.html',
        controller: 'oikeustulkkiCreateCtrl'
      })

      .when('/oikeustulkki/:id', {
        templateUrl: 'templates/oikeustulkkiView.html',
        controller: 'oikeustulkkiViewCtrl'
      })

      .when('/oikeustulkki/:id/edit', {
        templateUrl: 'templates/oikeustulkkiEdit.html',
        controller: 'oikeustulkkiEditCtrl'
      })

      .otherwise({
        redirect: '/search'
      });
}]);
