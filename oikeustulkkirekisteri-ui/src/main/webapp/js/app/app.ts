declare var angular:any;
declare var _:any;

const app = angular.module('registryApp', ['ngRoute', 'ngMessages', 'ui.select']).factory('Page', () => {
  let page = 'main';
  return {
    page: () => { return page; },
    setPage: (newPage) => { page = newPage; },
    activePage: (pageName) => { return page === pageName; }
  };
});
