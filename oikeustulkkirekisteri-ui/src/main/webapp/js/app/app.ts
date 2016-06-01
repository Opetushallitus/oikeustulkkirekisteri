declare var angular:any;
const app = angular.module('myApp', ['ngRoute']);

app.controller('mainCtrl', ($scope, Page) => {
  $scope.Page = Page;
  console.log("mainCtrl");
});

app.controller('translatorSearchCtrl', ($scope, Page) => {
  Page.setTitle('searchTranslator');
  console.log("translatorSearchCtrl");
});

app.controller('translatorCreateCtrl', ($scope, Page) => {
  Page.setTitle('addTranslator');
  console.log("translatorCreateCtrl");
});

app.controller('translatorViewCtrl', ($scope, Page) => {
  Page.setTitle('viewTranslator');
  console.log("translatorViewCtrl");
});

app.factory('Page', () => {
  let title = 'main';
  return {
    title: () => { title; },
    setTitle: (newTitle) => { title = newTitle; },
    activePage: (pageName) => { title === pageName; }
  };
});
