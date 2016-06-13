import {Kieli, Kielipari, kielipariMatch} from "../kielet.ts";
import {Tulkki, newTulkki, getTulkkiPostData} from "../tulkki.ts";

angular.module('registryApp').controller('oikeustulkkiCreateCtrl', ["$scope", "Page", "KoodistoService",
    "OikeustulkkiService", "$window", "$filter", ($scope, Page, KoodistoService, OikeustulkkiService, $window) => {
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

        $scope.save = () => {
            clearCustomErrors();
            $scope.showErrors = false;

            if (!_.isEmpty($scope.tulkkiForm.$error)) {
                $scope.showErrors = true;
                return;
            }

            OikeustulkkiService.createTulkki(getTulkkiPostData($scope.tulkki))
                .then(id =>$window.location.href = "#/oikeustulkki/" + id.data, error => {
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
        };
    }]);
