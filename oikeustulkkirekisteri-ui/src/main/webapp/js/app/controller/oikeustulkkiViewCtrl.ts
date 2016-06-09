angular.module('registryApp').controller('oikeustulkkiViewCtrl', ["$scope", "Page", "$location",
  ($scope, Page, $location) => {
  Page.setPage('viewOikeustulkki');
  console.log("oikeustulkkiViewCtrl");

  $scope.history = [{
    muokattuPvm: '1.6.2014',
    kommentti: 'Uusittu voimassaolo',
    visible: false
  },{
    muokattuPvm: '1.11.2015',
    kommentti: 'Lisätty kielipari',
    visible: false
  }];

  $scope.showItem = (item) => {
    item.visible = !item.visible;
  };

}]);