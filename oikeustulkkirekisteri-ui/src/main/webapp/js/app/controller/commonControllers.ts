angular.module('registryApp').controller('mainCtrl', ["$scope", "Page", "LocalisationService", "$route",
  ($scope, Page, LocalisationService, $route) => {
    $scope.Page = Page;

    $scope.switchLanguage = () => {
      const toLocale = LocalisationService.getLocale() === 'fi' ? 'sv' : 'fi';
      LocalisationService.setLocale(toLocale);
      $route.reload();
    };

  }]);