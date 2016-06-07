import {Kieli, Kielipari} from "../kielet.ts";

angular.module('registryApp').controller('oikeustulkkiSearchCtrl', ["$scope", "Page", "KoodistoService",
  ($scope, Page, KoodistoService) => {

  Page.setPage('searchOikeustulkki');
  $scope.showResults = false;

  $scope.kieliparit = [];
  $scope.kielesta = null;
  $scope.kieleen = null;
  $scope.kielet = [];

  KoodistoService.getKielet().then(r => {
    $scope.kielet = r.data;
    $scope.kielesta = {selected: $scope.kielet[0]};
    $scope.kieleen = {selected: $scope.kielet[1]};
  });

  $scope.search = () => {
    $scope.showResults = true;
  };

  $scope.addKielipari = () => {
    const kielipari:Kielipari = new Kielipari($scope.kielesta.selected, $scope.kieleen.selected);

    var kielipariAlreadyExists = _.some($scope.kieliparit, (kpari) => {
      return kielipari.isMatch(kpari);
    });

    if(kielipariAlreadyExists){
      console.error("kielipari jo lisätty");
      return;
    }

    $scope.kieliparit.push(kielipari);
  };

}]);
