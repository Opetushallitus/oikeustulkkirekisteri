import {Kieli, Kielipari, kielipariMatch} from "../kielet.ts";
import {Tulkki, newTulkki, getTulkkiPostData, isTulkkiKutsumanimiValid} from "../tulkki.ts";

angular.module('registryApp').controller('oikeustulkkiCreateCtrl', ["$scope", "Page", "KoodistoService",
    "OikeustulkkiService", "$window", "$filter", '$rootScope', 'LocalisationService',
            ($scope, Page, KoodistoService, OikeustulkkiService, $window, $filter, $rootScope, LocalisationService) => {
        Page.setPage('addOikeustulkki');
        $scope.showErrors = false;
        
        KoodistoService.getKielet().then(r => {
            $scope.kielet = r.data;
            $scope.kielesta = {selected: _.find($scope.kielet, {'arvo': 'FI'})};
            $scope.kieleen = {selected: $scope.kielet[1]};
        });

        $scope.tulkki = newTulkki();
        $scope.tulkki.alkaa = new Date();
        $scope.regions = [];

        KoodistoService.getMaakunnat().then(r => $scope.regions = r.data);
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
            $rootScope.$broadcast('clearErrors');
            clearCustomErrors();
            $scope.showErrors = false;

            checkIfKutsumanimiValid();
            if (!_.isEmpty($scope.tulkkiForm.$error)) {
                $rootScope.$broadcast('addError', $(".translations [tt='oikeustulkki_save_failed_missing_fields']").text());
                $scope.showErrors = true;
                return;
            }

            OikeustulkkiService.createTulkki(getTulkkiPostData($scope.tulkki))
                .then(id => {
                    $rootScope.$broadcast('addSuccess', $(".translations [tt='oikeustulkki_created']").text());
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
                    } else if (error.data.message) {
                        $rootScope.$broadcast('addError', error.data.message);
                    } else {
                        $rootScope.$broadcast('addError', $(".translations [tt='oikeustulkki_save_failed']").text());
                    }
                }
            );
        };
    }]);
