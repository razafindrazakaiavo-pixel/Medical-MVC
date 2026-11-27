-- Données fictives réservées aux démonstrations locales.
-- Mot de passe des comptes demo : DemoMedical123!
-- Les entrées existantes ne sont pas remplacées.
USE medical;
START TRANSACTION;

INSERT INTO patient (id, nom, prenom, naissance, genre, adresse, telephone)
SELECT '7c723081-45be-4d68-9ba8-cfa717fd7d01', 'Rakoto', 'Aina', '1995-05-12', 'Femme', 'Antananarivo', '0340000001'
WHERE NOT EXISTS (SELECT 1 FROM patient WHERE id = '7c723081-45be-4d68-9ba8-cfa717fd7d01');

INSERT INTO patient (id, nom, prenom, naissance, genre, adresse, telephone)
SELECT '7c723081-45be-4d68-9ba8-cfa717fd7d02', 'Rabe', 'Mamy', '1987-10-23', 'Homme', 'Antsirabe', '0340000002'
WHERE NOT EXISTS (SELECT 1 FROM patient WHERE id = '7c723081-45be-4d68-9ba8-cfa717fd7d02');

INSERT INTO patient (id, nom, prenom, naissance, genre, adresse, telephone)
SELECT '7c723081-45be-4d68-9ba8-cfa717fd7d03', 'Rasoa', 'Voahangy', '2001-03-17', 'Femme', 'Toamasina', '0340000003'
WHERE NOT EXISTS (SELECT 1 FROM patient WHERE id = '7c723081-45be-4d68-9ba8-cfa717fd7d03');

INSERT INTO utilisateur (login, role, patient_id, password_hash)
SELECT 'demo.medecin', 'MEDECIN', NULL, 'DemoMedical123!'
WHERE NOT EXISTS (SELECT 1 FROM utilisateur WHERE login = 'demo.medecin');

INSERT INTO utilisateur (login, role, patient_id, password_hash)
SELECT 'demo.aina', 'PATIENT', '7c723081-45be-4d68-9ba8-cfa717fd7d01', 'DemoMedical123!'
WHERE NOT EXISTS (SELECT 1 FROM utilisateur WHERE login = 'demo.aina' OR patient_id = '7c723081-45be-4d68-9ba8-cfa717fd7d01');

INSERT INTO utilisateur (login, role, patient_id, password_hash)
SELECT 'demo.mamy', 'PATIENT', '7c723081-45be-4d68-9ba8-cfa717fd7d02', 'DemoMedical123!'
WHERE NOT EXISTS (SELECT 1 FROM utilisateur WHERE login = 'demo.mamy' OR patient_id = '7c723081-45be-4d68-9ba8-cfa717fd7d02');

INSERT INTO utilisateur (login, role, patient_id, password_hash)
SELECT 'demo.voahangy', 'PATIENT', '7c723081-45be-4d68-9ba8-cfa717fd7d03', 'DemoMedical123!'
WHERE NOT EXISTS (SELECT 1 FROM utilisateur WHERE login = 'demo.voahangy' OR patient_id = '7c723081-45be-4d68-9ba8-cfa717fd7d03');

COMMIT;
0
