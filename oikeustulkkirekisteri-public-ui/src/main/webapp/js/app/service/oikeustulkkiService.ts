angular.module('publicRegistryApp').service('OikeustulkkiService', ['$http', '$q', ($http, $q) => {
  const root = '/oikeustulkkirekisteri-service/api/public/';

  const getTulkit = (termi:string, kieliparit, toimintaAlue) => {
    let params:any = {
      kieliparit: kieliparit,
      termi: termi
      // page: 1,
      // count: 5
    };

    if (toimintaAlue !== "00") {
      params.maakuntaKoodi = toimintaAlue;
    }

    return $http.post(root + 'hae', params);
  };

  const getTulkki = (id:number) => {
    return $http.get(root + id);
  };

  return {
    getTulkit: getTulkit,
    getTulkki: getTulkki
  };

}]);
