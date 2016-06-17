angular.module('publicRegistryApp').service('KoodistoService', ['$http', ($http) => {
  const root = '/oikeustulkkirekisteri-service/api/koodisto/';

  const getKielet = () => {
    return $http.get(root+'kielet');
  };

  const getMaakunnat = () => {
    return $http.get(root+'maakunnat');
  };

  return {
    getKielet:getKielet,
    getMaakunnat:getMaakunnat
  };
}]);
