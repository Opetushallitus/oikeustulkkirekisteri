angular.module('registryApp').controller('mainCtrl', ["$scope", "Page", "LocalisationService", "$route",
  ($scope, Page, LocalisationService, $route) => {

    $scope.Page = Page;
    $scope.currentLocale = LocalisationService.getLocale().toUpperCase();
    $scope.showHeader = true;

    //ng-include wont update localization otherwise
    const forceHeaderUpdate = () => {
      $scope.showHeader = false;
      $route.reload();
      setTimeout(()=> {
        $scope.showHeader = true;
        $route.reload();
      }, 1);
    };
    
    $scope.switchLanguage = () => {
      const toLocale = LocalisationService.getLocale() === 'fi' ? 'sv' : 'fi';
      LocalisationService.setLocale(toLocale);
      $scope.currentLocale = LocalisationService.getLocale().toUpperCase();
      $route.reload();
      forceHeaderUpdate();
    };

  }]);