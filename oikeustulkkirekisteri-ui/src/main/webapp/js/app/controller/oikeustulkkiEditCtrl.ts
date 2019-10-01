import {Kieli, Kielipari, kielipariMatch} from "../kielet.ts";
import {Tulkki, newTulkki, getTulkkiPostData, isTulkkiKutsumanimiValid} from "../tulkki.ts";
import moment = require("moment");

angular.module('registryApp').controller('oikeustulkkiEditCtrl', ["$scope", "$routeParams", "Page", "KoodistoService",
  "OikeustulkkiService", "$window", "$filter", '$rootScope', 'LocalisationService',
        ($scope, $routeParams, Page, KoodistoService, OikeustulkkiService, $window, $filter, $rootScope, LocalisationService) => {
    Page.setPage('editOikeustulkki');
    $scope.showErrors = false;
    $scope.tulkki = null;
    $scope.kieletByArvo = {};
    $scope.maakunnatByArvo = {};
    $scope.regions = [];
    $scope.action = $routeParams.action;
    $scope.tulkkiId = $routeParams.id;

    if ($scope.action != 'edit' && $scope.action != 'create') {
      $scope.action = 'create';
    }
    
    KoodistoService.getKielet().then(r => {
      $scope.kielet = r.data;
      
      $scope.kielesta = {selected: _.find($scope.kielet, {'arvo': 'FI'})};
      $scope.kieleen = {selected: $scope.kielet[1]};
      $scope.voimassaoloAlkaa = {selected: new Date()};
      $scope.voimassaoloPaattyy = {selected: moment().add(5, 'years').toDate()};

      _.map($scope.kielet, kp => $scope.kieletByArvo[kp.arvo] = kp);

      KoodistoService.getMaakunnat().then(r2 => {
        $scope.regions = r2.data;
        _.map($scope.regions, mk => $scope.maakunnatByArvo[mk.arvo] = mk);

        OikeustulkkiService.getTulkki($routeParams.id).then((results) => {
          var result = results.data;
          if ($scope.action == 'create') {
            delete result.id;
          }

          console.log("Result kieliparit lenght: " + result.kieliParit.length);

          result.kieliparit = _.map(result.kieliParit, (kielipari):Kielipari => {
            return {kielesta: $scope.kieletByArvo[kielipari.kielesta], kieleen: $scope.kieletByArvo[kielipari.kieleen], voimassaoloAlkaa: kielipari.voimassaoloAlkaa, voimassaoloPaattyy: kielipari.voimassaoloPaattyy};
          });

          console.log("Result kieliparit lenght mapped: " + result.kieliParit.length);

          result.toimintaAlue = _.map(result.maakunnat, maakunta => $scope.maakunnatByArvo[maakunta]);

          $scope.tulkki = result;
        });
      });
    });
    
    
    $scope.addKielipari = () => {
      const kielipari:Kielipari = {
        kielesta: $scope.kielesta.selected,
        kieleen: $scope.kieleen.selected,
        voimassaoloAlkaa: $scope.voimassaoloAlkaa.selected,
        voimassaoloPaattyy: $scope.voimassaoloPaattyy.selected
      };

      console.log("Result ADD kielipari" + JSON.stringify(kielipari));

      if (kielipari.kielesta != kielipari.kieleen) {
        var kielipariAlreadyExists = _.some($scope.tulkki.kieliparit, (kpari) => {
          return kielipariMatch(kielipari, kpari);
        });
        if (kielipariAlreadyExists) {
          console.error("kielipari jo lisätty");
          return;
        }
        $scope.tulkki.kieliparit.push(kielipari);
      }
    };

    $scope.removeKielipari = (kielipari:Kielipari) => _.remove($scope.tulkki.kieliparit, kielipari);

    const clearCustomErrors = () => {
      _.forEach($scope.tulkkiForm, (item)=> {
        if (item && item.$customError) {
          item.$customError = null;
        }
      });
    };

    const checkIfKutsumanimiValid = () => {
      let nameIsValid = isTulkkiKutsumanimiValid($scope.tulkki);
      $scope.tulkkiForm.kutsumanimi.$setValidity('kutsumainvalid', nameIsValid);
    };

    $scope.save = () => {
      $rootScope.$broadcast('clearErrors');
      clearCustomErrors();
      $scope.showErrors = false;

      checkIfKutsumanimiValid();
      if (!_.isEmpty($scope.tulkkiForm.$error) || !$scope.tulkki.kieliparit.length) {
        if ($scope.action == 'create') {
          $rootScope.$broadcast('addError', $(".translations [tt='oikeustulkki_save_failed_missing_fields']").text())
        } else {
          $rootScope.$broadcast('addError', $(".translations [tt='oikeustulkki_update_failed_missing_fields']").text())
        }
        $scope.showErrors = true;
        return;
      }
      
      if ($scope.action == 'create') {
        OikeustulkkiService.createTulkki(getTulkkiPostData($scope.tulkki))
            .then(id => {
              $rootScope.$broadcast('addSuccess', $(".translations [tt='oikeustulkki_created_existing']").text());
              $window.location.hash = "/oikeustulkki/" + id.data;
            }, error => {
                if (error.data.violations && error.data.violations.length) {
                  _.forEach(error.data.violations, (violation) => {
                    if (!$scope.tulkkiForm[violation.path]) {
                      console.log(violation);
                    } else {
                      $scope.tulkkiForm[violation.path].$customError = violation.message;
                      $scope.tulkkiForm[violation.path].$setValidity('custom', false);
                      $scope.showErrors = true;
                    }
                  });
                  $rootScope.$broadcast('addError', $(".translations [tt='oikeustulkki_save_failed']").text());
                } else if (error.data.messsage) {
                  $rootScope.$broadcast('addError', error.data.messsage);
                } else {
                  $rootScope.$broadcast('addError', $(".translations [tt='oikeustulkki_save_failed']").text());
                }
            });
      } else {
        OikeustulkkiService.updateTulkki(getTulkkiPostData($scope.tulkki))
            .then(() => {
              $rootScope.$broadcast('addSuccess', $(".translations [tt='oikeustulkki_updated']").text());
              $window.location.hash = "/oikeustulkki/" + $routeParams.id;
            }, error => {
                if (error.data.violations && error.data.violations.length) {
                  _.forEach(error.data.violations, (violation) => {
                    if (!$scope.tulkkiForm[violation.path]) {
                      console.log(violation);
                    } else {
                      $scope.tulkkiForm[violation.path].$customError = violation.message;
                      $scope.tulkkiForm[violation.path].$setValidity('custom', false);
                    }
                  });
                  $rootScope.$broadcast('addError', $(".translations [tt='oikeustulkki_update_failed']").text());
                } else if (error.data.message) {
                  $rootScope.$broadcast('addError', error.data.message);
                } else {
                  $rootScope.$broadcast('addError', $(".translations [tt='oikeustulkki_update_failed']").text());
                }
              }
          );
      }
    };
  }]);
