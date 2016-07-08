angular.module('registryApp').controller('mainCtrl', ["$scope", "Page", "LocalisationService", "$route", "$rootScope",
  ($scope, Page, LocalisationService, $route, $rootScope) => {

    $scope.Page = Page;
    $scope.currentLocale = LocalisationService.getLocale().toUpperCase();
    $scope.showHeader = true;
    $scope.errors = [];
    $rootScope.$on('addError', ($e,e) => $scope.errors.push({type:'error', message:e}));
    $rootScope.$on('addSuccess', ($e,msg) => $scope.errors.push({type:'success', message:msg, preserve:1}));
    $rootScope.$on('clearErrors', ($e,preserve=false) => {
      var filtered = [];
      $.map($scope.errors, msg => {
        if (preserve && msg.preserve-- > 0) {
          msg.preserve--;
          filtered.push(msg);
        }
      });
      console.info('filtered', filtered);
      $scope.errors = filtered;
    });
    $scope.errorRead = (i:number) => $scope.errors.splice(i, 1);
    
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
      $scope.errrors = [];
      const toLocale = LocalisationService.getLocale() === 'fi' ? 'sv' : 'fi';
      LocalisationService.setLocale(toLocale);
      $scope.currentLocale = LocalisationService.getLocale().toUpperCase();
      $route.reload();
      forceHeaderUpdate();
    };

  }]);