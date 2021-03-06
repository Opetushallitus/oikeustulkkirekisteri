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
import com.wordnik.swagger.annotations.ApiOperation;
import fi.vm.sade.oikeustulkkirekisteri.service.AppSettingsService;
import fi.vm.sade.oikeustulkkirekisteri.service.dto.AppSettingsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
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
    public Map<String,Object> testLoggedIn() throws IOException {
        appSettingsService.requireAuthentication();
        Map<String,Object> resultJson = new HashMap<>();
        resultJson.put("loggedIn", true);
        return resultJson;
    }
    
    @ResponseBody
    @RequestMapping(value  =  "/testLoggedInAndRole", method  =  RequestMethod.GET)
    public Map<String,Object> testLoggedInAndRole() throws IOException {
        appSettingsService.requireAuthenticationAndCrud();
        Map<String,Object> resultJson = new HashMap<>();
        resultJson.put("loggedIn", true);
        return resultJson;
    }
    
    @ResponseBody
    @RequestMapping(value = "/sessionMaxInactiveInterval", method = RequestMethod.GET)
    @ApiOperation(value = "Palauttaa session erääntymisen aikarajan sekunteina",
            notes = "Tarvitsee HTTP kutsun, jossa on session id",
            response = String.class)
    public String maxInactiveInterval(@Context HttpServletRequest req) {
        return Integer.toString(req.getSession().getMaxInactiveInterval());
    }
}
