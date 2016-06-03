declare var angular:any;

const app = angular.module('registryApp', ['ngRoute']).factory('Page', () => {
  let page = 'main';
  return {
    page: () => { return page; },
    setPage: (newPage) => { page = newPage; },
    activePage: (pageName) => { return page === pageName; }
  };
});

const app = angular.module('registryApp', ['ngResource', 'ngRoute', 'ui.select']);
