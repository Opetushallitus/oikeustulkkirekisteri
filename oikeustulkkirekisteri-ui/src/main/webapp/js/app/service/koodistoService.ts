angular.module('registryApp').service('KoodistoService', ['$http', ($http) => {
    const root = '/oikeustulkkirekisteri-service/api/koodisto/';
    
    const getKielet = () => {
        return $http.get(root+'/kielet');
    };
    
    return {
        getKielet:getKielet
    };
}]);
