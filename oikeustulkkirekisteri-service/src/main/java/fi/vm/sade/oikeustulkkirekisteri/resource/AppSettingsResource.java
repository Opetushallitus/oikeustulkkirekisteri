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

package fi.vm.sade.oikeustulkkirekisteri.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wordnik.swagger.annotations.Api;
import fi.vm.sade.oikeustulkkirekisteri.service.AppSettingsService;
import fi.vm.sade.oikeustulkkirekisteri.settings.dto.AppSettingsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * User: ratamaa
 * Date: 3/18/14
 * Time: 12:41 PM
 */
@Api("Asetukset")
@Controller
@Scope(value =  WebApplicationContext.SCOPE_APPLICATION)
@RequestMapping(value  =  "/app")
public class AppSettingsResource {
    @Autowired
    private AppSettingsService appSettingsService;

    @ResponseBody
    @RequestMapping(value  =  "/settings.js", method  =  RequestMethod.GET)
    public String settingsJs() throws IOException {
        AppSettingsDto settings  =  appSettingsService.getUiSettings();
        return "window.CONFIG  =  "  +  new ObjectMapper().writeValueAsString(settings)  +  ";";
    }
    
    @ResponseBody
    @RequestMapping(value  =  "/testLoggedIn", method  =  RequestMethod.GET)
    public void testLoggedIn() throws IOException {
        appSettingsService.requireAuthentication();
    }

    @ResponseStatus(value = HttpStatus.UNAUTHORIZED) // 401 Not authorized
    @ExceptionHandler(AccessDeniedException.class) @ResponseBody
    public Map<String,Object> notAuthorized(HttpServletRequest req, AccessDeniedException exception) {
        return new HashMap<>();
    }
}
