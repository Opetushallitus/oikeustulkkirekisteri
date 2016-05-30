package fi.vm.sade.oikeustulkkirekisteri.service.impl;

import fi.vm.sade.oikeustulkkirekisteri.repository.OikeustulkkiRepository;
import fi.vm.sade.oikeustulkkirekisteri.service.OikeustulkkiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * User: tommiratamaa
 * Date: 30.5.2016
 * Time: 14.11
 */
@Service
public class OikeustulkkiServiceImpl implements OikeustulkkiService {
    @Autowired
    private OikeustulkkiRepository oikeustulkkiRepository;

    
}
