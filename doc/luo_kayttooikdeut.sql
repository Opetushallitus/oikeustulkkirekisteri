-- authentication-kantaan:

begin;

insert into text_group(id, version) values ((select nextval('hibernate_sequence')*10), 0);
insert into text(id, version, lang, text, textgroup_id) values ((select nextval('hibernate_sequence')*10), 0,
                                                                'FI', 'Oikeustulkkirekisteri', (select max(id) from text_group));
insert into text(id, version, lang, text, textgroup_id) values ((select nextval('hibernate_sequence')*10), 0,
                                                                'SV', 'Oikeustulkkirekisteri', (select max(id) from text_group));
insert into text(id, version, lang, text, textgroup_id) values ((select nextval('hibernate_sequence')*10), 0,
                                                                'EN', 'Oikeustulkkirekisteri', (select max(id) from text_group));
insert into palvelu (id, version, name, palvelutyyppi, textgroup_id)
values ((select nextval('hibernate_sequence')*10), 0, 'OIKEUSTULKKIREKISTERI', 'YKSITTAINEN', (select max(id) from text_group));


insert into text_group(id, version) values ((select nextval('hibernate_sequence')*10), 0);
insert into text(id, version, lang, text, textgroup_id) values ((select nextval('hibernate_sequence')*10), 0,
                                                                'FI', 'Oikeustulkkirekisterin ylläpito', (select max(id) from text_group));
insert into text(id, version, lang, text, textgroup_id) values ((select nextval('hibernate_sequence')*10), 0,
                                                                'SV', 'Oikeustulkkirekisterin ylläpito', (select max(id) from text_group));
insert into text(id, version, lang, text, textgroup_id) values ((select nextval('hibernate_sequence')*10), 0,
                                                                'EN', 'Oikeustulkkirekisterin ylläpito', (select max(id) from text_group));
insert into kayttooikeus(id, version, palvelu_id, rooli, textgroup_id)
values ((select nextval('hibernate_sequence')*10), 0, (select id from palvelu where name = 'OIKEUSTULKKIREKISTERI'),
        'OIKEUSTULKKI_CRUD', (select max(id) from text_group));

commit;
