declare const angular:any;
declare const _:any;
declare const jQuery:any;
declare const $:any;

if (!window['CONFIG']) {
  window['CONFIG'] = window['CONFIG'] || {env: {}};
}

const app = angular.module('registryApp', ['ngRoute', 'ngMessages', 'ui.bootstrap', 'ngIdle', 'angularModalService',
                        'ngCookies', 'localisation', 'ui.select', 'datePicker']).factory('Page', ['$rootScope',($rootScope) => {
  let page = 'main';
  return {
    page: () => {
      return page;
    },
    setPage: (newPage) => {
      $rootScope.$broadcast('clearErrors', true);
      page = newPage;
    },
    activePage: (pageName) => {
      return page === pageName;
    }
  };
}]).config(['uiSelectConfig', (uiSelectConfig) => {
  uiSelectConfig.theme = 'bootstrap';
  uiSelectConfig.matcher = (term:string, text:string) => term && text.toLowerCase().substr(0, term.length) == term.toLowerCase();
}]).run(['$http', '$cookies', ($http, $cookies) => {
  $http.defaults.headers.common['clientSubSystemCode'] = "oikeustulkkirekisteri.oikeustulkkirekisteri-ui.frontend";
  if($cookies['CSRF']) {
    $http.defaults.headers.common['CSRF'] = $cookies['CSRF'];
  }
}]).directive('idle', ['Idle', 'ModalService', function(Idle, ModalService) {
  return {
    restrict: 'A',
    link: function(scope, elem, attrs) {
      var openModal = function(template) {
        return ModalService.showModal({
          templateUrl: template,
          controller: 'SessionExpiresCtrl'
        }).then(function(modal) {
          modal.element.modal();
          modal.close.then(function(result) {
            $(".modal-backdrop").remove();
            Idle.watch();
          });
        });
      };

      scope.$on('IdleStart', function () {
        console.info('IdleStart');
        scope.sessionWarning = openModal('templates/sessiontimeout/sessionWarning.html');
      });
      scope.$on('IdleTimeout', function() {
        console.info('IdleTimeout');
        if (scope.sessionWarning && scope.sessionWarning.close) {
          scope.sessionWarning.close();
        }
        scope.sessionWarning = openModal('templates/sessiontimeout/sessionExpired.html');
        Idle.unwatch();
      });
    }
  };
}]).constant('SessionTimeoutConfig', {
  SESSION_KEEPALIVE_INTERVAL_IN_SECONDS: 30,
  MAX_SESSION_IDLE_TIME_IN_SECONDS: 1800,
  WARNING_DURATION_IN_SECONDS: 300
}).config(['IdleProvider', 'KeepaliveProvider', 'SessionTimeoutConfig', function(IdleProvider, KeepaliveProvider, SessionTimeoutConfig) {
  IdleProvider.idle(SessionTimeoutConfig.MAX_SESSION_IDLE_TIME_IN_SECONDS - SessionTimeoutConfig.WARNING_DURATION_IN_SECONDS);
  IdleProvider.timeout(SessionTimeoutConfig.WARNING_DURATION_IN_SECONDS);
  IdleProvider.windowInterrupt('focus');
  
  KeepaliveProvider.interval(SessionTimeoutConfig.SESSION_KEEPALIVE_INTERVAL_IN_SECONDS);
  KeepaliveProvider.http(window['CONFIG'].env['session.max.inactive.interval'] || '/oikeustulkkirekisteri-service/api/app/sessionMaxInactiveInterval');
}]).run(['Idle', function(Idle) {
  console.info('Start Idle watch');
  Idle.watch();
}]).controller('SessionExpiresCtrl', ['Idle', '$scope', 'close', '$window',
          'LocalisationService', 'SessionTimeoutConfig', function(Idle, $scope, close, $window, 
                                                                  LocalisationService, SessionTimeoutConfig) {
  $scope.timeoutMessage = function() {
    var duration = Math.floor(SessionTimeoutConfig.MAX_SESSION_IDLE_TIME_IN_SECONDS / 60);
    return LocalisationService.getTranslation("session_expired_text1_part1") + " " + duration +  " " 
        + LocalisationService.getTranslation("session_expired._ext1_part2");
  };
  $scope.okConfirm = function() {
    close(null, 200);
  };
  $scope.redirectToLogin = function() {
    $window.location.reload();
    close(null, 200);
  };
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
}).factory('RequestsErrorHandler', ['$q', '$location',
  ($q, $location) => {
    console.info('config', window['CONFIG']);
    const authenticationUrl = (window['CONFIG'].env['cas.login'] || '/cas/login') + '?service=' + $location.$$absUrl;
    return {
      responseError: (rejection) => {
        if (rejection.data && rejection.data.errorType && rejection.data.errorType === 'AccessDeniedException') {
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
        $("body").removeClass('loading');
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
    const showRoleError = () => {
      console.info('Required role missing.');
      const requestRoleLink = window['CONFIG'].env['host.base-uri'] + "authentication-henkiloui/html/#/omattiedot";
      $("body").append($('<div><div><div id="header"><img src="img/opetushallitus.gif"><p>Oikeustulkkirekisteri</p></div></div><div><div class="container"><div class="message-holder error-message">Ei käyttöoikeutta. Vaadittu käyttöoikeusrooli puuttuu. <a href="'+requestRoleLink+'">Oikeuksia voit anoa täältä.</a></div></div></div></div>'));
    };
    jQuery.ajax(window['CONFIG'].env['test.logged.in.url'] || '/oikeustulkkirekisteri-service/api/app/testLoggedIn', {
      crossDomain:true,
      complete: logRequest,
      success: function(data, status) {
        if (!data) {
          redirectToLogin();
        } else {
          try {
            var json = data instanceof String ? JSON.parse(data) : data;
            if (json && json.loggedIn) {
              console.info('User logged in. Testing required role...');
              jQuery.ajax(window['CONFIG'].env['test.logged.in.and.role.url'] || '/oikeustulkkirekisteri-service/api/app/testLoggedInAndRole', {
                crossDomain: true,
                complete: logRequest,
                success: function (data, status) {
                  if (!data) {
                    showRoleError();
                  } else {
                    try {
                      var json = data instanceof String ? JSON.parse(data) : data;
                      if (json && json.loggedIn) {
                        console.info('User has required role');
                        initFunction("Login test", data, status);
                      } else {
                        showRoleError();
                      }
                    } catch (e) {
                      showRoleError();
                    }
                  }
                },
                statusCode: {302: showRoleError},
                error: showRoleError
              });
            } else {
              redirectToLogin();
            }
          } catch(e) {
            redirectToLogin();
          }
        }
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
};
