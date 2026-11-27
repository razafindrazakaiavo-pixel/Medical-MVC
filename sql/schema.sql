CREATE DATABASE IF NOT EXISTS medical CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE medical;

CREATE TABLE IF NOT EXISTS patient (
    id CHAR(36) NOT NULL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    naissance DATE NOT NULL,
    genre VARCHAR(20) NOT NULL,
    adresse VARCHAR(300),
    telephone VARCHAR(30)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS utilisateur (
    login VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL PRIMARY KEY,
    role VARCHAR(20) NOT NULL,
    patient_id CHAR(36) NULL UNIQUE,
    password_hash VARCHAR(200) NOT NULL,
    CONSTRAINT fk_utilisateur_patient FOREIGN KEY (patient_id)
        REFERENCES patient (id) ON DELETE CASCADE,
    CONSTRAINT chk_utilisateur_role CHECK (role IN ('MEDECIN', 'PATIENT'))
) ENGINE=InnoDB;

