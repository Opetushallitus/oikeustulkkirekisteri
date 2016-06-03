angular.module('registryApp').service('KoodistoService', ['$http', ($http) => {
    const root = '/oikeustulkkirekisteri-service/api/koodisto/';

    const getKielet = () => {
        return $http.get(root+'/kielet');
    };

    const getMaakunnat = () => {
        return $http.get(root+'/maakunnat');
    };

    const getKunnat = () => {
        return $http.get(root+'/kunnat');
    };

    return {
        getKielet:getKielet,
        getMaakunnat:getMaakunnat,
        getKunnat:getKunnat
    };
}]);
