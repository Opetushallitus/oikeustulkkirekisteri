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

package fi.vm.sade.oikeustulkkirekisteri.service.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * User: ratamaa
 * Date: 3/18/14
 * Time: 12:52 PM
 */
public class AppSettingsDto implements Serializable {
    private static final long serialVersionUID = 817543818821400959L;
    
    private Map<String, Object> env  =  new HashMap<String, Object>();
    private Map<String, Object> app  =  new HashMap<String, Object>();

    public AppSettingsDto() {
    }

    public Map<String, Object> getEnv() {
        return env;
    }

    public void setEnv(Map<String, Object> env) {
        this.env  =  env;
    }

    public Map<String, Object> getApp() {
        return app;
    }

    public void setApp(Map<String, Object> app) {
        this.app  =  app;
    }
}
