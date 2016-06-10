declare var angular:any;
declare var _:any;

const app = angular.module('registryApp', ['ngRoute', 'ngMessages', 'ui.select', 'datePicker']).factory('Page', () => {
  let page = 'main';
  return {
    page: () => { return page; },
    setPage: (newPage) => { page = newPage; },
    activePage: (pageName) => { return page === pageName; }
  };
});

angular.module('registryApp').filter('selectFilter', () => {
  return (items, input) => {
    if (input.length === 0) {
      return items;
    }

    return _.filter(items, (item) => {
      return item.nimi.FI.toLowerCase().indexOf(input.toLowerCase()) === 0;
    });
  };
});
