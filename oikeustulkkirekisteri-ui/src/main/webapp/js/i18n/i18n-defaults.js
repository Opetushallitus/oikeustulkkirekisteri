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
        generic_cancel: 'Peruuta',
        generic_tallenna: 'Tallenna',
        generic_edit: 'Muokkaa',
        generic_remove: 'Poista',
        generic_show: 'Näytä',

        hallinta: 'Oikeustulkkirekisteri - Hallinta',
        oikeustulkkirekisteri: 'Oikeustulkkirekisteri',
        toinen_kieli: 'På svenska',

        oikeustulkki_aidinkieli: 'Äidinkieli',
        oikeustulkki_lisaa: 'Lisää tulkki',
        oikeustulkki_alku_pvm: 'Alku pvm',
        oikeustulkki_alue_koko_suomi: 'Koko Suomi',
        oikeustulkki_alue_maakunnat: 'Maakunnat',
        oikeustulkki_etunimet: 'Etunimet',
        oikeustulkki_hetu: 'Hetu',
        oikeustulkki_katusosoite: 'Katuosoite',
        oikeustulkki_kielipari_lisaa: 'Lisää',
        oikeustulkki_kieliparit: 'Kieliparit',
        oikeustulkki_korkeakoulu: 'Korkeakoulututkinto',
        oikeustulkki_kutsumanimi: 'Kutsumanimi',
        oikeustulkki_lisatietoa: 'Lisätietoa',
        oikeustulkki_muu_yhteystieto: 'Muu yhteystieto',
        oikeustulkki_postinumero: 'Postinumero',
        oikeustulkki_postitoimipaikka: 'Postitoimipaikka',
        oikeustulkki_puhelinnumero: 'Puhelinnumero',
        oikeustulkki_sahkopostiosoite: 'Sähköpostiosoite',
        oikeustulkki_sukunimi: 'Sukunimi',
        oikeustulkki_suoritettu_tutkinto: 'Suoritettu tutkinto',
        oikeustulkki_toimintaalue: 'Toiminta-alue',
        oikeustulkki_tutkinto_erikoisammattitutkinto: 'Oikeustulkin erikoisammattitutkinto',
        oikeustulkki_historia: 'Historia',
        oikeustulkki_julkaistu: 'Julkaistu',
        oikeustulkki_koulutus: 'Koulutus',
        oikeustulkki_muokkaushistoria: 'Muokkaushistoria',
        oikeustulkki_osoite: 'Osoite',
        oikeustulkki_rekisterointi_alkupvm: 'Rekisteröinnin alkupvm',
        oikeustulkki_rekisterointi_loppupvm: 'Rekisteröinnin loppupvm',
        oikeustulkki_tiedot: 'Oikeustulkin tiedot',
        oikeustulkki_yhteystiedot: 'Yhteystiedot',
        oikeustulkki_katuosoite: 'Katuosoite',
        oikeustulkki_paattyy_pvm: 'Päättyy pvm',

        pakollinen_kentta: 'Pakollinen kenttä',

        virhe_aidinkieli_valitsematta: 'Äidinkieltä ei ole valittu',
        virhe_kielipareja_ei_lisatty: 'Kielipareja ei ole lisätty',
        virhe_sahkoposti_virheellinen: 'Virheellinen sähköpostiosoite',

        haku: 'Haku',
        haku_ei_tuloksia: 'Haulla ei löytynyt tuloksia',
        haku_hae: 'Hae',
        haku_hetu: 'Hetu',
        haku_kieliparit: 'Kieliparit',
        haku_koko_suomi: 'Koko Suomi',
        haku_nimi: 'Nimi',
        haku_suoritettu_tutkinto: 'Suoritettu tutkinto',
        haku_termi: 'Nimi/Hetu/Henkilönumero',
        haku_toimintaalue: 'Toiminta-alue',
        haku_voimassa: 'Voimassa',

        nayta_uusin: 'Näytä uusin',
        poista_tiedot: 'Poista tiedot',
        poista_varmistus: 'Haluatko varmasti poistaa tulkin?',
        poisto_epaonnistui: 'Tietojen poistaminen epäonnistui',
        uusi_hakemus: 'Uusi hakemus',
        julkaistu: 'saa julkaista',
        lisaa_uusi_hakemus: 'Lisää oikeustulkille uusi hakemus',
        muokkaa_tulkkia: 'Muokkaa oikeustulkkia',
        muokkausviesti: 'Muokkausviesti',
        
        kieli_fi: 'suomi',
        kieli_sv: 'ruotsi',
        kieli_en: 'englanti',

        session_expired_text1_part1: 'Istuntosi on vanhentunut ',
        session_expired_text1_part2: ' minuutin käyttämättömyyden johdosta.'
    };
    defaultValues['en'] = defaultValues['fi'];
    defaultValues['sv'] = defaultValues['fi'];
    $provider.value("i18nDefaults", defaultValues);
}]);
