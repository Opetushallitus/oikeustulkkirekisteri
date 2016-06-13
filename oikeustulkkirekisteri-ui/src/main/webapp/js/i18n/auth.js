/*
 * Copyright (c) 2013 The Finnish Board of Education - Opetushallitus
 *
 * This program is free software:  Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 */

// Apinoitu valintaperusteet / auth.js


/**
 * Authentication module.
 * NOTE: data (pre)loaded at server startup in index.hrml to Config.env["cas.userinfo"]
 */

var auth = angular.module("auth", ['ngResource', 'config']);
var USER = "USER_";
var READ = "_READ";
var UPDATE = "_READ_UPDATE";
var CRUD = "_CRUD";

auth.factory('MyRolesModel', ["$q", "$http", "$timeout", function($q, $http, $timeout) {
    return window.CONFIG.env["me"] || {};
}]);

auth.factory('AuthService', ["$q", "MyRolesModel", function($q, MyRolesModel) {
    return {
        /**
         * Palauttaa käyttäjän kielen
         */
        getLanguage: function() {
            return MyRolesModel.lang || "fi";
        }
    };
}]);

