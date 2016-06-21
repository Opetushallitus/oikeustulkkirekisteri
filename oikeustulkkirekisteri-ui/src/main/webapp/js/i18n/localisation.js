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

/**
 * Localisation support for Angular apps.
 * + registers module "localisation".
 *
 * NOTE: this module assumes that all the translations are PRELOADED in
 * global scope to configuration object:
 * </b>Config.env["oikeustulkkirekisteri.localisations"]</b>
 *
 * See "index.html" how the pre-loading is done to following global variable:
 * <b>window.CONFIG.env["oikeustulkkirekisteri.localisations"]</b>
 *
 * @see index.html for preload implementation
 *
 * @author mlyly
 */
var localisation = angular.module('localisation', ['ngResource', 'config', 'I18n', 'auth']);

/**
 * "Localisations" factory, returns resource for operating on localisations.
 */
localisation.factory('Localisations', ["$log", "$resource", "Config", function($log, $resource, Config) {
    var uri = Config.env.localisationRestUrl;
    $log.info("Localisations() - uri = ", uri);

    return $resource(uri + "/:id", {
        id: '@id'
    }, {
        update: {
            method: 'PUT',
            withCredentials: true,
            headers: {'Content-Type': 'application/json; charset=UTF-8'}
        },
        save: {
            method: 'POST',
            withCredentials: true,
            headers: {'Content-Type': 'application/json; charset=UTF-8'}
        },
        updateAccessed: {
            method: 'PUT',
            withCredentials: true,
            headers: {'Content-Type': 'application/json; charset=UTF-8'}
        }
    });

}]);

/**
 * UI-directive for using translations.
 *
 * usage:
 * <pre>
 *   &lt;div tt="this.is.translation.key"><b>Oletusarvo käännösavaimelle!</b></div&gt;
 *   &lt;div tt="this.is.translation.key2" locale="ab">Abhasi oletusarvo</div&gt;
 *
 *   {{ t("this.is.another.key" }}
 *   {{ t("this.is.another.key", ["param", "param too"]) }}
 *
 *   {{ tl("this.is.another.key", "sv" }}
 *   {{ tl("this.is.anotker.key", "sv", ["param", "param too"]) }}
 * </pre>
 */
localisation.directive('tt', ['$log', 'LocalisationService', 'i18nDefaults',
            function($log, LocalisationService, i18nDefaults) {
    return {
        restrict: 'A',
        replace: true,
        //template: '<div tt="this.is.key" locale="fi">Default saved for the given key</div>',
        scope: false,
        compile: function(tElement, tAttrs, transclude) {
            // $log.info("tt compile", tElement, tAttrs, transclude);

            var key = tAttrs["tt"];
            var locale = angular.isDefined(tAttrs["locale"]) ? tAttrs["locale"] : LocalisationService.getLocale();
            var translation = "";

            if (LocalisationService.hasTranslation(key, locale)) {
                // Existing translations, just return it
                translation = LocalisationService.tl(key, locale);
            } else {
                // Missing / new translation
                // Grab the original / placeholder text in the template
                var originalText = "";

                var localName = tElement[0].localName;
                if (localName === "input") {
                    originalText = tAttrs["value"];
                } else {
                    originalText = tElement.html();
                }
                if( !originalText ) {
                    originalText = i18nDefaults[key];
                }

                if ( window.CONFIG.mode && window.CONFIG.mode == 'dev-without-backend' ) {
                     translation = originalText;
                } else {
                    LocalisationService.createMissingTranslations(key, locale, originalText);

                    translation = originalText;
                }
            }

            // $log.info("  key: '" + key + "', locale: '"+ locale + "' --> " + translation);

            // Put translated text to DOM
            if (localName === "input") {
                tElement.attr("value", translation);
            } else {
                tElement.html(translation);
            }

            return function postLink(scope, iElement, iAttrs, controller) {
                // $timeout(scope.$destroy.bind(scope), 0);
            };
        }
    };
}]);



/**
 * Singleton service for localisations.
 *
 * Usage:
 * <pre>
 * LocalisationService.t("this.is.the.key")  == localized value in users locale
 * LocalisationService.t("this.is.the.key2", ["array", "of", "values"])  == localized value in users locale
 *
 * LocalisationService.tl("this.is.the.key", "fi")  == localized value in given locale
 * LocalisationService.tl("this.is.the.key2", "fi", ["array", "of", "values"])  == localized value in given locale
 * </pre>
 */
localisation.service('LocalisationService', ["$log", "$q", "$http", "$interval", "Localisations",
                "Config", "AuthService", "i18nDefaults",
        function($log, $q, $http, $interval, Localisations, Config, AuthService, i18nDefaults) {
    $log.log("LocalisationService()");

    // Singleton state, default current locale for the user
    this.locale = AuthService.getLanguage();

    $log.info("  user locale = " + this.locale);

    /**
     * Get users locale OR default locale "fi".
     *
     * @returns {String}
     */
    this.getLocale = function() {
        // Default fallback
        if (this.locale === undefined) {
            $log.warn("  aha! undefined locale - using fi!");
            this.locale = "fi";
        }
        return this.locale;
    };

    // Note - the "koodisto" kieli... Brilliant!
    var kieliUri = "kieli_" + this.getLocale();

    /**
     * returns language uri that matches the current language
     */
    this.getKieliUri = function(){
      return kieliUri;
    };

    this.setLocale = function(value) {
        this.locale = value;
    };

    /**
     * Collect updates to "accessed" information here, flushed every 5 minutes
     */
    this.updateAccessedById = {};

    this.updateAccessInformation = function() {
        var uri = Config.env.localisationRestUrl + "/access";

        var ids = Object.keys(this.updateAccessedById);
        this.updateAccessedById = {};

        $log.info("updateAccessInformation, ids=" + ids, ids);
        if (angular.isDefined(ids)) {
            Localisations.updateAccessed({ id: "access" }, ids, function () {
                $log.info("success!");
            }, function () {
                $log.info("failed!");
            });
        }
    };

    // Localisations: MAP[locale][key] = {key, locale, value};
    // This map is used for quick access to the localisation (which are in list)
    // see this.updateLookupMap() how it is filled
    this.localisationMapByLocaleAndKey = {};

    /**
     * Get translation, fill in possible parameters.
     *
     * @param {String} key
     * @param {String} locale, if undefined get it vie getLocale()
     * @param {Array} params
     * @returns {String} translation value, parameters replaced
     */
    this.getTranslation = function(key, locale, params) {
        // $log.info("getTranslation(key, locale, params), key=" + key + ", l=" +  locale + ",params=" + params);

        // Use default locale if not specified
        if (locale === undefined) {
            locale = this.getLocale();
        }

        var result = this.getRawTranslation(key, locale);

        if (!angular.isDefined(result)) {
            // Should not happen, missing translations created automatically elsewhere...
            result = "!!Virhe!! key=" + key + " - locale=" + locale;
        } else {
            // Expand parameters
            if (params != undefined) {
                result = result.replace(/{(\d+)}/g, function(match, number) {
                    return angular.isDefined(params[number]) ? params[number] : match;
                });
            }
        }

        // $log.info("getTranslation(key, locale, params), key=" + key + ", l=" +  locale + ",params=" + params + " --> " + result);
        return result;
    };

    this.isEmpty = function(str) {
        return (!str || 0 === str.length);
    };

    this.isBlank = function(str) {
        return (!str || /^\s*$/.test(str));
    };

    /**
     * @param {type} key
     * @param {type} locale
     * @returns {boolean} True if there is translaton map for given locale AND an exisiting translation for given key
     */
    this.hasTranslation = function(key, locale) {
        var translation = this.getRawTranslation(key, locale);
        return angular.isDefined(translation);
    };

    /**
     * Get the "raw" translations, no parameters filled.
     *
     * @param {type} key
     * @param {type} locale
     * @returns {v.value}
     */
    this.getRawTranslation = function(key, locale) {
        // Get translations by locale
        var v0 = this.localisationMapByLocaleAndKey[locale];
        // Get translation by key
        var v = v0 ? v0[key] : undefined;

        // Update access info
        if (v) {
            // Bookkeeping for accessed updating, only the keys are used.
            if (angular.isDefined(v.id)) {
                this.updateAccessedById[v.id] = "";
            } else {
                $log.info("WTF? tranlation id is undefined...? t=" + v, v);
            }
        }

        // Get value if any
        var result = v ? v.value : undefined;
        return result;
    };

    /**
     * Saves cache entry to MAP[locale][key] = newValue;
     *
     * @see method getRawTranslation(key, locale) - it gets it from this map.
     *
     * @param {type} key
     * @param {type} locale
     * @param {type} newEntry
     * @returns {undefined}
     */
    this.putCachedLocalisation = function(key, locale, newEntry) {
        // Create map entries
        this.localisationMapByLocaleAndKey = this.localisationMapByLocaleAndKey || {};
        this.localisationMapByLocaleAndKey[locale] = this.localisationMapByLocaleAndKey[locale] || {};
        this.localisationMapByLocaleAndKey[locale][key] = newEntry;

        // Update in memory storage for local translations (loaded from server)
        this.getTranslations().push(newEntry);
    };



    /**
     * Creates missing translations AND stores temp values to local "cache".
     * Default translations created are "fi", "en" and "sv" for any given key.
     *
     * @param {type} key
     * @param {type} locale
     * @param {type} originalText
     * @returns {undefined}
     */
    this.createMissingTranslations = function(key, locale, originalText, params) {
        $log.info("createMissingTranslations()", key, locale, originalText);

        // Default locales that translations should be created for
        var locales = ["fi", "en", "sv"];

        if (angular.isDefined(locale) && locales.indexOf(locale) < 0) {
            locales.push(locale);
        }

        // Create translations to serverside AND update in memory map for transations
        var self = this;

        angular.forEach(locales, function(l) {
            // Is the translation really missing?
            if (!self.hasTranslation(key, l)) {
                // Ok, really missing translation then

                // Save this to server
                var newEntry = {category: "oikeustulkkirekisteri", key: key, locale: l, value: originalText};

                $log.info("SAVING to server: ", newEntry);

                Localisations.save(newEntry,
                        function(data) {
                            $log.info("  created new translation to server side! data = ", data);
                        },
                        function(data, status, headers, config) {
                            $log.warn("  FAILED to created new translation to server side! ", data, status, headers, config);
                        });

                // Create temporary placeholder for next requests
                self.putCachedLocalisation(key, l, newEntry);
            }
        });

        if (params != undefined) {
            originalText = originalText.replace(/{(\d+)}/g, function(match, number) {
                return angular.isDefined(params[number]) ? params[number] : match;
            });
        }

        return originalText;
    };


    /**
     * Get list of currently loaded translations.
     *
     * @returns global APP_LOCALISATION_DATA, array of {key, locale, value} objects.
     */
    this.getTranslations = function() {
        Config.env["oikeustulkkirekisteri.localisations"] = Config.env["oikeustulkkirekisteri.localisations"] || [];
        return Config.env["oikeustulkkirekisteri.localisations"];
    };


    /**
     * Loop over all translations, create new lookup map to store translations to
     * MAP[locale][translation_key] == {key, locale, value};
     *
     * @returns created lookup map and stores it to local services variable
     */
    this.updateLookupMap = function() {
        $log.info("updateLookupMap()");

        // Create temporary map
        var tmp = {};

        for (var localisationIndex in Config.env["oikeustulkkirekisteri.localisations"]) {
            var localisation = Config.env["oikeustulkkirekisteri.localisations"][localisationIndex];
            var mapByLocale = tmp[localisation.locale];
            if (!mapByLocale) {
                tmp[localisation.locale] = {};
                mapByLocale = tmp[localisation.locale];
            }
            mapByLocale[localisation.key] = localisation;
        }

        // Use the new map
        this.localisationMapByLocaleAndKey = tmp;

        $log.info("===> result ", this.localisationMapByLocaleAndKey);
        return this.localisationMapByLocaleAndKey;
    };

    /**
     * Get translation value. Assumes use of current UI locale (LocalisationService.getLocale())
     * If translation with current locale and key is not found then new translation entry / entries will be created.
     *
     * @param {String} key
     * @param {Array} params
     * @returns {String} value for translation
     */
    this.t = function(key, params) {
        return this.tl(key, this.getLocale(), params);
    };

    /**
     * Get translation in given locale.
     * If translation with current locale and key is not found then new translation entry / entries will be created.
     *
     * @param {type} key translation key
     * @param {type} locale desired locale
     * @param {type} params ARRAY of parameters
     *
     * @returns Resolved translation with "{NUM}" replaced with parameters
     */
    this.tl = function(key, locale, params) {

        //
        if (angular.isDefined(params)) {
            if (params instanceof Array) {
                // OK
            } else {
                // Make it so...
                // $log.info("FIXING PARAMS TO BE ARRAY: ", params);
                params = [ params ];
            }
        }

        if (this.hasTranslation(key, locale)) {
            return this.getTranslation(key, locale, params);
        } else {
            var result = "[" + key + "-" + locale + "]";
            if( angular.isDefined(i18nDefaults[locale])
                    && angular.isDefined(i18nDefaults[locale][key]) ) {
                result = i18nDefaults[locale][key];
            }

            if ( window.CONFIG.mode && window.CONFIG.mode == 'dev-without-backend' ) {
                if (params != undefined) {
                    result = result.replace(/{(\d+)}/g, function(match, number) {
                        return angular.isDefined(params[number]) ? params[number] : match;
                    });
                }
                return result;
            }

            // Missing translation, create it, returns placeholder value given in
            return this.createMissingTranslations(key, locale, result, params);
        }
    };

    // Bootstrap in memory lookup table
    this.updateLookupMap();

    $log.info("LocalisationService - initialising... done.");
}]);

/**
 * LocalisationCtrl - a localisation controller.
 * An easy way to bind "t" function to global scope. (now attached in "body")
 */
localisation.controller('LocalisationCtrl', ["$scope", "LocalisationService", "$log", "$interval", "Config",
        function($scope, LocalisationService, $log, $interval, Config) {
    $log.info("LocalisationCtrl()");

    $scope.CONFIG = Config;

    // Returns translation if it exists
    $scope.t = function(key, params) {
        return LocalisationService.t(key, params);
    };

    // Get translation in given locale
    $scope.tl = function(key, locale, params) {
        return LocalisationService.tl(key, locale, params);
    };

    /**
     * Updates used translations every five minutes.
     *
     * @type @call;$interval
     */
    var timer = $interval(function () {
        LocalisationService.updateAccessInformation();
    }, 5 * 60 * 1000);

    $scope.$on("$destroy", function() {
        $log.info("LocalisationCtrl() -  $destroy");
        if (timer) {
            $interval.cancel(timer);
            timer = null;
        }
        LocalisationService.updateAccessInformation();
    });

}]);
