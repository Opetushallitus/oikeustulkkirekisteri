import {Tulkki} from "../tulkki.ts";

angular.module('registryApp').service('OikeustulkkiService', ['$http', ($http) => {
  const root = '/oikeustulkkirekisteri-service/api/oikeustulkki/';

  const getTulkit = () => {
    return $http.get(root);
  };

  const createTulkki = (tulkki: Tulkki) => {
    return $http.post(root, tulkki);
  };

  return {
    getTulkit: getTulkit,
    createTulkki: createTulkki
  };

}]);
