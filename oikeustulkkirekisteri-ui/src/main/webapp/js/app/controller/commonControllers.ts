// TODO:refactor to different files (and rename translator -> oikeustulkki):

angular.module('registryApp').controller('mainCtrl', ($scope, Page) => {
    $scope.Page = Page;
    console.log("mainCtrl");
}).controller('translatorSearchCtrl', ($scope, Page, KoodistoService) => {
    Page.setPage('searchTranslator');
    $scope.showResults = false;
    console.log("translatorSearchCtrl");

    $scope.kielesta = 'FI';
    $scope.kieleen = null;
    $scope.kielet = [];
    KoodistoService.getKielet().then(r => {
        $scope.kielet = r.data;
        console.info('kielet', $scope.kielet);
    });
    
    $scope.search = () => {
        $scope.showResults = true;
    };
}).controller('translatorCreateCtrl', ($scope, Page) => {
    Page.setPage('addTranslator');

    $scope.kielesta;
    $scope.kieleen;
    $scope.kieliparit = [];

    $scope.addLanguagePair = () => {
        $scope.kieliparit.push(new Kielipari($scope.kielesta, $scope.kieleen));
    };
}).controller('translatorViewCtrl', ($scope, Page) => {
    Page.setPage('viewTranslator');
    console.log("translatorViewCtrl");
});
