declare const angular:any;
declare const _:any;
declare const jQuery:any;
declare const $:any;

if (!window['CONFIG']) {
  window['CONFIG'] = window['CONFIG'] || {env: {}};
}

const app = angular.module('registryApp', ['ngRoute', 'ngMessages', 'ngCookies', 'localisation', 'ui.select', 'datePicker']).factory('Page', () => {
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
}]).run(['$http', '$cookies', ($http, $cookies) => {
  $http.defaults.headers.common['clientSubSystemCode'] = "oikeustulkkirekisteri.oikeustulkkirekisteri-ui.frontend";
  if($cookies['CSRF']) {
    $http.defaults.headers.common['CSRF'] = $cookies['CSRF'];
  }
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
});

angular.module('registryApp').factory('RequestsErrorHandler', ['$q', '$location',
  ($q, $location) => {
    const authenticationUrl = (window['CONFIG'].env['cas.login'] || '/cas/login') + '?service=' + $location.$$absUrl;
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

window['appInit'] = () => {
  var init_counter = 0;
  var fail = false;

  jQuery.support.cors = true;

  function initFail(id, xhr, status) {
    fail = true;
    console.log("Init failure: " + id + " -> "+status, xhr);
  }

  function initFunction(id, xhr, status) {
    init_counter--;
    console.log("Got ready signal from: " + id + " -> "+status+" -> IC="+init_counter/*, xhr*/);
    if (!fail && init_counter == 0) {
      angular.element(document).ready(function() {
        angular.module('registryAppInitialized', ['registryApp']);
        angular.bootstrap(document, ['registryAppInitialized']);
      });
    }
  }

  function logRequest(xhr, status) {
    console.log("LOG "+status+": "+xhr.status+" "+xhr.statusText, xhr);
  }

  if ( !(window['CONFIG'].mode && window['CONFIG'].mode == 'dev-without-backend') ) {
    // Ensure logged in
    init_counter++;
    const redirectToLogin = () => {
      const authenticationUrl = (window['CONFIG'].env['cas.login'] || '/cas/login') + '?service=' + window.location.href;
      console.info('Login test failed.');
      window.location.href = authenticationUrl;
    };
    jQuery.ajax(window['CONFIG'].env['test.logged.in.url'] || '/oikeustulkkirekisteri-service/api/app/testLoggedIn', {
      crossDomain:true,
      complete: logRequest,
      success: function(xhr, status) {
        initFunction("Login test", xhr, status);
      },
      statusCode: { 302: redirectToLogin },
      error: redirectToLogin
    });
    
    // Preload application localisations
    var localisationUrl = window['CONFIG'].env.localisationRestUrl
        + "?category=oikeustulkkirekisteri&value=cached";
    console.log("** Loading localisation info; from: ", localisationUrl);
    init_counter++;
    jQuery.ajax(localisationUrl, {
      dataType: "json",
      crossDomain:true,
      complete: logRequest,
      success: function(xhr, status) {
        window['CONFIG'].env["oikeustulkkirekisteri.localisations"] = xhr;
        initFunction("localisations", xhr, status);
      },
      error: function(xhr, status) {
        window['CONFIG'].env["oikeustulkkirekisteri.localisations"] = [];
        initFail("localisations", xhr, status);
      }
    });
  } else {
    init_counter++;
    initFunction('dev', {}, 200);
  }
}