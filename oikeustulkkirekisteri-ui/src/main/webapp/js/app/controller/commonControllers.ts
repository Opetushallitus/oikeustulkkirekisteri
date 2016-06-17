angular.module('registryApp').controller('mainCtrl', ["$scope", "Page", "LocalisationService", "$route",
  ($scope, Page, LocalisationService, $route) => {
    $scope.Page = Page;

    $scope.currentLocale = LocalisationService.getLocale().toUpperCase();

    $scope.switchLanguage = () => {
      const toLocale = LocalisationService.getLocale() === 'fi' ? 'sv' : 'fi';
      LocalisationService.setLocale(toLocale);
      $scope.currentLocale = LocalisationService.getLocale().toUpperCase();
      $route.reload();
    };

  }]);