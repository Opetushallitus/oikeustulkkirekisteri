angular.module('registryApp').controller('oikeustulkkiSearchCtrl', ($scope, Page, KoodistoService) => {
  Page.setPage('searchOikeustulkki');
  $scope.showResults = false;

  $scope.kielesta = 'FI';
  $scope.kieleen = null;
  $scope.kielet = [];
  KoodistoService.getKielet().then(r => {
    $scope.kielet = r.data;
  });

  $scope.search = () => {
    $scope.showResults = true;
  };
});
