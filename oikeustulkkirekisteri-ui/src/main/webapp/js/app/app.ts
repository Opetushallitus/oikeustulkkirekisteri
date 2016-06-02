declare var angular:any;
const app = angular.module('registryApp', ['ngRoute']);

class Kielipari{
  kielesta: string;
  kieleen: string;

  constructor(kielesta: string, kieleen: string){
    this.kielesta = kielesta;
    this.kieleen = kieleen;
  }
}

app.controller('mainCtrl', ($scope, Page) => {
  $scope.Page = Page;
  console.log("mainCtrl");
});

app.controller('translatorSearchCtrl', ($scope, Page) => {
  Page.setPage('searchTranslator');
  $scope.showResults = false;
  console.log("translatorSearchCtrl");

  $scope.search = () => {
    $scope.showResults = true;
  };
});

app.controller('translatorCreateCtrl', ($scope, Page) => {
  Page.setPage('addTranslator');

  $scope.kielesta;
  $scope.kieleen;
  $scope.kieliparit = [];

  $scope.addLanguagePair = () => {
    $scope.kieliparit.push(new Kielipari($scope.kielesta, $scope.kieleen));
  };

});

app.controller('translatorViewCtrl', ($scope, Page) => {
  Page.setPage('viewTranslator');
  console.log("translatorViewCtrl");
});

app.factory('Page', () => {
  let page = 'main';
  return {
    page: () => { return page; },
    setPage: (newPage) => { page = newPage; },
    activePage: (pageName) => { return page === pageName; }
  };
});
