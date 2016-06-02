declare var angular:any;

const app = angular.module('registryApp', ['ngRoute']).factory('Page', () => {
  let page = 'main';
  return {
    page: () => { return page; },
    setPage: (newPage) => { page = newPage; },
    activePage: (pageName) => { return page === pageName; }
  };
});

class Kielipari{
  kielesta: string;
  kieleen: string;

  constructor(kielesta: string, kieleen: string){
    this.kielesta = kielesta;
    this.kieleen = kieleen;
  }
}
