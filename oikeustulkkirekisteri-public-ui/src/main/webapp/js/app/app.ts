declare const angular:any;
declare const _:any;

const app = angular.module('publicRegistryApp', ['ngRoute', 'ui.select']);

// console.log('testttttttttttttt');
angular.module('publicRegistryApp').controller('mainCtrl', ["$scope", ($scope) => {

  console.log('test');
  // $scope.switchLanguage = () => {
  //   const toLocale = LocalisationService.getLocale() === 'fi' ? 'sv' : 'fi';
  //   LocalisationService.setLocale(toLocale);
  //   $route.reload();
  // };

}]);

angular.module('publicRegistryApp').filter('selectFilter', () => {
  return (items, input) => {
    if (input.length === 0) {
      return items;
    }

    return _.filter(items, (item) => {
      return item.nimi.FI.toLowerCase().indexOf(input.toLowerCase()) === 0;
    });
  };
});

angular.module('publicRegistryApp').filter('selectFilter', () => {
  return (items, input) => {
    if (input.length === 0) {
      return items;
    }

    return _.filter(items, (item) => {
      return item.nimi.FI.toLowerCase().indexOf(input.toLowerCase()) === 0;
    });
  };
});

