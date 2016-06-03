declare var angular:any;

const app = angular.module('registryApp', ['ngResource', 'ngRoute', 'ui.select']);

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

app.controller('translatorViewCtrl', ($scope, Page) => {
  Page.setPage('viewTranslator');
  console.log("translatorViewCtrl");
});

app.factory('Page', () => {
  let page = 'main';
  return {
    page: () => {
      return page;
    },
    setPage: (newPage) => {
      page = newPage;
    },
    activePage: (pageName) => {
      return page === pageName;
    }
  };
});
