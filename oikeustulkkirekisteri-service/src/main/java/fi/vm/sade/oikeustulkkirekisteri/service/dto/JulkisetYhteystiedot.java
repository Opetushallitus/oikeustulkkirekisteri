package fi.vm.sade.oikeustulkkirekisteri.service.dto;

/**
 * User: tommiratamaa
 * Date: 20.6.2016
 * Time: 14.38
 */
public interface JulkisetYhteystiedot {
    /**
     * @param email tai null jos ei julkaisulupaa
     */
    void setEmail(String email);

    /**
     * @param puhelinnumero tai null jos ei julkaisulupaa tai ei tiedossa
     */
    void setPuhelinnumero(String puhelinnumero);

    /**
     * @param muuYhteystieto tai null jos ei julkaisulupaa tai ei tiedossa
     */
    void setMuuYhteystieto(String muuYhteystieto);
}
