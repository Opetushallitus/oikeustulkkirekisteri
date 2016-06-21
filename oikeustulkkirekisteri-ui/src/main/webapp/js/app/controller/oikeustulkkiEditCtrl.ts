import {Kieli, Kielipari, kielipariMatch} from "../kielet.ts";
import {Tulkki, newTulkki, getTulkkiPostData, isTulkkiKutsumanimiValid} from "../tulkki.ts";

angular.module('registryApp').controller('oikeustulkkiEditCtrl', ["$scope", "$routeParams", "Page", "KoodistoService",
  "OikeustulkkiService", "$window", "$filter", ($scope, $routeParams, Page, KoodistoService, OikeustulkkiService, $window) => {
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
      _.map($scope.kielet, kp => $scope.kieletByArvo[kp.arvo] = kp);

      KoodistoService.getMaakunnat().then(r2 => {
        $scope.regions = r2.data;
        _.map($scope.regions, mk => $scope.maakunnatByArvo[mk.arvo] = mk);

        OikeustulkkiService.getTulkki($routeParams.id).then((results) => {
          var result = results.data;
          if ($scope.action == 'create') {
            delete result.id;
          }
          result.kieliparit = _.map(result.kieliParit, (kielipari):Kielipari => {
            return {kielesta: $scope.kieletByArvo[kielipari.kielesta], kieleen: $scope.kieletByArvo[kielipari.kieleen]};
          });
          result.toimintaAlue = _.map(result.maakunnat, maakunta => $scope.maakunnatByArvo[maakunta]);

          if ($scope.action == 'create') {
            result.alkaa = new Date();
            result.paattyy = null;
          } else {
            result.alkaa = new Date(result.alkaa[0], result.alkaa[1]-1, result.alkaa[2]);
            result.paattyy = new Date(result.paattyy[0], result.paattyy[1]-1, result.paattyy[2]);
          }
          $scope.tulkki = result;
        });
      });
    });
    
    
    $scope.addKielipari = () => {
      const kielipari:Kielipari = {
        kielesta: $scope.kielesta.selected,
        kieleen: $scope.kieleen.selected
      };
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
      clearCustomErrors();
      $scope.showErrors = false;

      checkIfKutsumanimiValid();
      if (!_.isEmpty($scope.tulkkiForm.$error)) {
        $scope.showErrors = true;
        return;
      }

      if ($scope.action == 'create') {
        OikeustulkkiService.createTulkki(getTulkkiPostData($scope.tulkki))
            .then(id => $window.location.href = "#/oikeustulkki/" + id.data, error => {
                if (error.data.violations) {
                  _.forEach(error.data.violations, (violation) => {

                    if (!$scope.tulkkiForm[violation.path]) {
                      console.log(violation);
                    } else {
                      $scope.tulkkiForm[violation.path].$customError = violation.message;
                      $scope.tulkkiForm[violation.path].$setValidity('custom', false);
                      $scope.showErrors = true;
                    }
                  });
                }
              });
      } else {
        OikeustulkkiService.updateTulkki(getTulkkiPostData($scope.tulkki))
            .then(() => $window.location.href = "#/oikeustulkki/" + $routeParams.id, error => {
              if (error.data.violations) {
                _.forEach(error.data.violations, (violation) => {

                  if (!$scope.tulkkiForm[violation.path]) {
                    console.log(violation);
                  } else {
                    $scope.tulkkiForm[violation.path].$customError = violation.message;
                    $scope.tulkkiForm[violation.path].$setValidity('custom', false);
                  }
                });
              }
            });
      }
    };
  }]);
