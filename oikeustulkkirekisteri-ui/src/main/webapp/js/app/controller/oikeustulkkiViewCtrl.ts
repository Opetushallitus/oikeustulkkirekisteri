angular.module('registryApp').controller('oikeustulkkiViewCtrl', ["$scope", "Page", ($scope, Page) => {
  Page.setPage('viewOikeustulkki');
  console.log("oikeustulkkiViewCtrl");
}]);