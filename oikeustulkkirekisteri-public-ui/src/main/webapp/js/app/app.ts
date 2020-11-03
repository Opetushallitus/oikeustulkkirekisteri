declare const angular:any;
declare const _:any;

const app = angular.module('publicRegistryApp', ['ngRoute', 'ui.select', 'pascalprecht.translate']);

angular.module('publicRegistryApp').controller('mainCtrl', ["$scope", "$translate", ($scope, $translate) => {
  $translate.use('fi');
  $scope.currentLanguage = $translate.proposedLanguage().toUpperCase();
  
  $scope.switchLanguage = () => {
    const lang = $translate.proposedLanguage() === 'fi' ? 'sv' : 'fi';
    $translate.use(lang);
    $scope.currentLanguage = lang.toUpperCase();
  };
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

angular.module('publicRegistryApp').run(['$http', ($http) => {
  $http.defaults.headers.common['Caller-Id'] = "1.2.246.562.10.00000000001.oikeustulkkirekisteri-public-ui";
}]);
