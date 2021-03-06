import {Tulkki} from "../tulkki.ts";

angular.module('registryApp').service('OikeustulkkiService', ['$http', ($http) => {
  const root = '/oikeustulkkirekisteri-service/api/oikeustulkki/';

  const getTulkit = (termi:string, kieliparit:any, tutkintoTyyppi: string) => {
    const kieliparitHaku = _.map(kieliparit, (kielipari) => {
      return {'kielesta': kielipari.kielesta.arvo, 'kieleen': kielipari.kieleen.arvo}
    });

    const params:any = {
      termi: termi,
      page: 1,
      count: 5,
      kieliparit: kieliparitHaku,
      tutkintoTyyppi: tutkintoTyyppi
    };

    return $http.post(root+'hae', params);
  };

  const createTulkki = (tulkki:Tulkki) => {
    return $http.post(root, tulkki);
  };

  const getTulkki = (id:number) => {
    return $http.get(root + id);
  };

  const removeTulkki = (id:number) => {
    return $http.delete(root + id);
  };
  
  const updateTulkki = (tulkki) => {
    return $http.put(root, tulkki);
  };

  return {
    getTulkit: getTulkit,
    getTulkki: getTulkki,
    removeTulkki: removeTulkki,
    createTulkki: createTulkki,
    updateTulkki: updateTulkki
  };

}]);
