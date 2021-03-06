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

package fi.vm.sade.oikeustulkkirekisteri.service;

import fi.vm.sade.oikeustulkkirekisteri.service.dto.AppSettingsDto;
import org.springframework.security.access.prepost.PreAuthorize;

import static fi.vm.sade.oikeustulkkirekisteri.service.Constants.CRUD_PERMISSION;
import static fi.vm.sade.oikeustulkkirekisteri.service.Constants.PUBLIC;

/**
 * User: ratamaa
 * Date: 3/18/14
 * Time: 12:52 PM
 */
public interface AppSettingsService {
    /**
     * @return the env and app settings for UI
     */
    @PreAuthorize(PUBLIC)
    AppSettingsDto getUiSettings();

    @PreAuthorize("isAuthenticated() && " + CRUD_PERMISSION)
    void requireAuthenticationAndCrud();
    
    @PreAuthorize("isAuthenticated()")
    void requireAuthentication();
}
