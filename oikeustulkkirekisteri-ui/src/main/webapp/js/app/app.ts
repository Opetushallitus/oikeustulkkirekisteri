declare var angular:any;
declare var _:any;

const app = angular.module('registryApp', ['ngRoute', 'ngMessages', 'ui.select', 'datePicker']).factory('Page', () => {
  let page = 'main';
  return {
    page: () => {
      return page;
    },
    setPage: (newPage) => {
      page = newPage;
    },
    activePage: (pageName) => {
      return page === pageName;
    }
  };
}).config(['uiSelectConfig', (uiSelectConfig) => {
  uiSelectConfig.theme = 'bootstrap';
  uiSelectConfig.matcher = (term:string, text:string) => term && text.toLowerCase().substr(0, term.length) == term.toLowerCase();
}]);

angular.module('registryApp').filter('selectFilter', () => {
  return (items, input) => {
    if (input.length === 0) {
      return items;
    }

    return _.filter(items, (item) => {
      return item.nimi.FI.toLowerCase().indexOf(input.toLowerCase()) === 0;
    });
  };
})

angular.module('registryApp').factory('RequestsErrorHandler', ['$q', '$location',
  ($q, $location) => {
    const authenticationUrl = $location.$$absUrl + 'cas/login?service=' + $location.$$absUrl;
    return {
      responseError: (rejection) => {
        if (rejection.data.errorType === 'AccessDeniedException') {
          window.location.href = authenticationUrl;
        }
        return $q.reject(rejection);
      }
    };
  }]).config(['$provide', '$httpProvider', ($provide, $httpProvider) => {
  $httpProvider.interceptors.push('RequestsErrorHandler');
}]);
