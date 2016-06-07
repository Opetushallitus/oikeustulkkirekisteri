import {Kieli, Kielipari} from "../kielet.ts";


angular.module('registryApp').controller('oikeustulkkiSearchCtrl', ($scope, Page, KoodistoService) => {
angular.module('registryApp').controller('oikeustulkkiSearchCtrl', ["$scope", "Page", "KoodistoService", ($scope, Page, KoodistoService) => {
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
}]);
