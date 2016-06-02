angular.module('registryApp').config(($routeProvider) => {
  $routeProvider

      .when('/search', {
        templateUrl: 'templates/translatorSearch.html',
        controller: 'translatorSearchCtrl'
      })

      .when('/addTranslator', {
        templateUrl: 'templates/translatorCreate.html',
        controller: 'translatorCreateCtrl'
      })

      .when('/translator/:id', {
        templateUrl: 'templates/translatorView.html',
        controller: 'translatorViewCtrl'
      })

      .otherwise({
        redirect: '/search'
      });
});
