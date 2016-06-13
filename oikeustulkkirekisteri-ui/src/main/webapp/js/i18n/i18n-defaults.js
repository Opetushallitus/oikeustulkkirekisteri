/*
 * Copyright (c) 2013 The Finnish National Board of Education - Opetushallitus
 *
 * This program is free software: Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * European Union Public Licence for more details.
 */

angular.module("I18n", [], ["$provide", function($provider) {
    var defaultValues = {};
    defaultValues['fi'] = {
        loading: 'Ladataan...',
        confirm_yes: 'Kyllä',
        confirm_no: 'Ei',
        
        oikeustulkki_aidinkieli: 'Äidinkieli',
        
        kieli_fi: 'suomi',
        kieli_sv: 'ruotsi',
        kieli_en: 'englanti'
    };
    defaultValues['en'] = defaultValues['fi'];
    defaultValues['sv'] = defaultValues['fi'];
    $provider.value("i18nDefaults", defaultValues);
}]);
