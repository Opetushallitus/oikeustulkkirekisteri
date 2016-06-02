class Kielipari {
  kielesta:string;
  kieleen:string;

  constructor(kielesta:string, kieleen:string) {
    this.kielesta = kielesta;
    this.kieleen = kieleen;
  }
}

angular.module('registryApp').controller('translatorCreateCtrl', ($scope, Page) => {

  Page.setPage('addTranslator');

  $scope.regions = [
    {id: 1, name: 'Pirkanmaa'},
    {id: 2, name: 'Lappi'},
    {id: 3, name: 'Uusimaa'},
    {id: 4, name: 'Satakunta'}
  ];

  $scope.selected = {value: $scope.regions[0]};
  $scope.kielesta;
  $scope.kieleen;
  $scope.kieliparit = [];

  $scope.addKielipari = () => {
    $scope.kieliparit.push(new Kielipari($scope.kielesta, $scope.kieleen));
  };

});
