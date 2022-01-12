declare const angular:any;
declare const _:any;
declare const jQuery:any;

const getCookieValue = (name) => {
  const values = document.cookie.match('(^|;)\\s*' + name + '\\s*=\\s*([^;]+)');
  return values ? values.pop() : '';
}

jQuery.ajaxSetup({
  headers: { 'Caller-Id': '1.2.246.562.10.00000000001.oikeustulkkirekisteri-ui' },
  beforeSend: function (xhr) {
    xhr.setRequestHeader('CSRF', getCookieValue('CSRF'));
  }
});

const app = angular.module('publicRegistryApp', ['ngRoute', 'ngCookies', 'ui.select', 'pascalprecht.translate']);

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

angular.module('publicRegistryApp').run(['$http', '$cookies', ($http, $cookies) => {
  $http.defaults.headers.common['Caller-Id'] = "1.2.246.562.10.00000000001.oikeustulkkirekisteri-public-ui";
  $http.defaults.headers.common['CSRF'] = $cookies.get('CSRF') || '';
}]);
