package fi.vm.sade.oikeustulkkirekisteri.service;

/**
 * User: tommiratamaa
 * Date: 7.6.2016
 * Time: 18.48
 */
public interface Constants {
    String CRUD_PERMISSION = "isAuthenticated() and hasRole('ROLE_APP_OIKEUSTULKKIREKISTERI_OIKEUSTULKKI_CRUD')";
    String PUBLIC = "permitAll()";
}
