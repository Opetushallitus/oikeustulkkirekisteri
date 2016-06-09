import {Kieli, Kielipari} from "../kielet.ts";
import {Tulkki} from "../tulkki.ts";

angular.module('registryApp').controller('oikeustulkkiEditCtrl', ["$scope", "Page", "KoodistoService", "OikeustulkkiService",
  "$filter", ($scope, Page, KoodistoService, OikeustulkkiService, $filter) => {

  Page.setPage('editOikeustulkki');



}]);
