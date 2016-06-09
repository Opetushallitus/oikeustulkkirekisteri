import {Kieli, Kielipari} from "../kielet.ts";
import {Tulkki} from "../tulkki.ts";

angular.module('registryApp').controller('oikeustulkkiCreateCtrl', ["$scope", "Page", "KoodistoService", "OikeustulkkiService",
  "$filter", ($scope, Page, KoodistoService, OikeustulkkiService, $filter) => {

  Page.setPage('addOikeustulkki');
  $scope.showErrors = false;
  $scope.kielesta;
  $scope.kieleen;

  KoodistoService.getKielet().then(r => {
    $scope.kielet = r.data;
    $scope.kielesta = {selected: $scope.kielet[0]};
    $scope.kieleen = {selected: $scope.kielet[1]};
  });

  $scope.tulkki = new Tulkki();
  //TODO lisätään datepicker
  $scope.tulkki.alkaa = $filter('date')(new Date(), 'd.M.yyyy');
  $scope.regions = [];

  KoodistoService.getMaakunnat().then(r => {
    const regionsWithoutUnknownOption = _.filter(r.data, (region) => {
      return region.arvo !== "99";
    });
    $scope.regions = regionsWithoutUnknownOption;
  });

  $scope.addKielipari = () => {
    const kielipari:Kielipari = new Kielipari($scope.kielesta.selected, $scope.kieleen.selected);

    var kielipariAlreadyExists = _.some($scope.tulkki.kieliparit, (kpari) => {
      return kielipari.isMatch(kpari);
    });

    if(kielipariAlreadyExists){
      console.error("kielipari jo lisätty");
      return;
    }

    $scope.tulkki.kieliparit.push(kielipari);
  };

  $scope.removeKielipari = (kielipari:Kielipari) => _.remove($scope.tulkki.kieliparit, kielipari);

  $scope.save = () => {
    OikeustulkkiService.createTulkki($scope.tulkki.getTulkkiPostData());

    if (!_.isEmpty($scope.tulkkiForm.$error)) {
      $scope.showErrors = true;
    }
    console.log('save', $scope.tulkki);
  };

  $scope.addAllRegions = () => {
    $scope.tulkki.toimintaAlue = $scope.regions;
  };

}]);
