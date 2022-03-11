INSERT INTO USER(id, account_non_expired, account_non_locked, credentials_non_expired, email, enabled, forename, password, role, surname, company_id) VALUES (1, 'TRUE', 'TRUE', 'TRUE', "admin@admin", 'TRUE', "admin", "password", "ADMIN", "admin", null);
INSERT INTO USER(id, account_non_expired, account_non_locked, credentials_non_expired, email, enabled, forename, password, role, surname, company_id) VALUES (5, 'TRUE', 'TRUE', 'TRUE', "m@m", 'TRUE', "Maximilius", "password", "STUDENT", "Schniedelus", 4);
INSERT INTO USER(id, account_non_expired, account_non_locked, credentials_non_expired, email, enabled, forename, password, role, surname, company_id) VALUES (6, 'TRUE', 'TRUE', 'TRUE', "j@j", 'TRUE', "Jonas", "password", "STUDENT", "Speckmann", 2);
INSERT INTO USER(id, account_non_expired, account_non_locked, credentials_non_expired, email, enabled, forename, password, role, surname, company_id) VALUES (8, 'TRUE', 'TRUE', 'TRUE', "f@f", 'TRUE', "Felix", "password", "STUDENT", "Vielesorgen", 4);
INSERT INTO USER(id, account_non_expired, account_non_locked, credentials_non_expired, email, enabled, forename, password, role, surname, company_id) VALUES (8, 'TRUE', 'TRUE', 'TRUE', "adminTest@admin", 'FALSE', "adminTest", "password", "ADMIN", "adminTest", null);
INSERT INTO USER(id, account_non_expired, account_non_locked, credentials_non_expired, email, enabled, forename, password, role, surname, company_id) VALUES (8, 'TRUE', 'TRUE', 'TRUE', "studentTest@student", 'FALSE', "studentTest", "password", "STUDENT", "studentTest", null);


INSERT INTO COMPANY(id, description, name, company_logo_id) VALUES (2,"","[187]Strassenbande", null)
INSERT INTO COMPANY(id, description, name, company_logo_id) VALUES (3,"","Streber GmbH", null)
INSERT INTO COMPANY(id, description, name, company_logo_id) VALUES (4,"","7Bags", null)