angular.module('registryApp').controller('mainCtrl', ["$scope", "Page", ($scope, Page) => {
    $scope.Page = Page;
}]);