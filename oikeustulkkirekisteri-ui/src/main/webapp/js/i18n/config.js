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

/*
 * Help:
 * Add service factory(/js/shared/config.js) to your module.
 * Module name : 'config'.
 * Factory name : 'Config'.
 *
 * FAQ:
 * How to get an environment variable by a key: <factory-object>.env[<string-key>].
 * How to get AngularJS application variable by a key: <factory-object>.app[<string-key>].
 *
 * Example:
 * cfg.env["koodi-uri.koulutuslaji.nuortenKoulutus"];
 * result value : "koulutuslaji_n"
 */
angular.module('config', []).factory('Config', function() {
    var factoryObj = (function() {
        var ENV_CONF = 'env'; //system properties from common properties files, service uris etc.
        var APP_CONF = 'app'; //AngularJS application properties
        var DEV_CONF = 'developerConfigLocation'; //AngularJS developer properties

        globalConfig = window.CONFIG;

        if (globalConfig === null || typeof globalConfig === 'undefined')
            throw "Configuration variable cannot be null.";

        if (globalConfig[ENV_CONF] === null || typeof globalConfig[ENV_CONF] === 'undefined')
            throw "Environment data cannot be null.";

        if (globalConfig[APP_CONF] === null || typeof globalConfig[APP_CONF] === 'undefined')
            throw "Angular application data cannot be null.";

        var output = {};
        output[ENV_CONF] = angular.copy(globalConfig[ENV_CONF]);
        output[APP_CONF] = angular.copy(globalConfig[APP_CONF]);

        return output;
    }());
    return factoryObj;
});

