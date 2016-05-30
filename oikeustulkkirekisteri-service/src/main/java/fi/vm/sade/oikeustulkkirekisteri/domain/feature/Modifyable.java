package fi.vm.sade.oikeustulkkirekisteri.domain.feature;

import org.joda.time.DateTime;

/**
 * User: tommiratamaa
 * Date: 30.5.2016
 * Time: 13.44
 */
public interface Modifyable {
    DateTime getMuokattu();
    
    void setMuokattu(DateTime at);
    
    String getMuokkaaja();
    
    void setMuokkaaja(String oid);
}
