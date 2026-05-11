CREATE DATABASE IF NOT EXISTS forage;
USE forage;

-- Table Client
CREATE TABLE IF NOT EXISTS client (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(255) NOT NULL,
    mdp VARCHAR(255) NOT NULL,
    contact VARCHAR(20),
    adresse VARCHAR(500)
);

-- Table Region
CREATE TABLE IF NOT EXISTS region (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    libelle VARCHAR(255) NOT NULL
);

-- Table District
CREATE TABLE IF NOT EXISTS district (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    libelle VARCHAR(255) NOT NULL,
    id_region BIGINT NOT NULL,
    FOREIGN KEY (id_region) REFERENCES region(id) ON DELETE CASCADE
);

-- Table Commune
CREATE TABLE IF NOT EXISTS commune (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    libelle VARCHAR(255) NOT NULL,
    id_district BIGINT NOT NULL,
    FOREIGN KEY (id_district) REFERENCES district(id) ON DELETE CASCADE
);

-- Table Statut
CREATE TABLE IF NOT EXISTS statut (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    libelle VARCHAR(255) NOT NULL
);

-- Table Demande
CREATE TABLE IF NOT EXISTS demande (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    id_client BIGINT NOT NULL,
    id_commune BIGINT NOT NULL,
    lieu VARCHAR(255) NOT NULL,
    FOREIGN KEY (id_client) REFERENCES client(id) ON DELETE CASCADE,
    FOREIGN KEY (id_commune) REFERENCES commune(id) ON DELETE CASCADE
);

-- Table DemandeStatut
CREATE TABLE IF NOT EXISTS demande_statut (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    id_statut BIGINT NOT NULL,
    id_demande BIGINT NOT NULL,
    description VARCHAR(500),
    date TIMESTAMP NOT NULL,
    FOREIGN KEY (id_statut) REFERENCES statut(id) ON DELETE CASCADE,
    FOREIGN KEY (id_demande) REFERENCES demande(id) ON DELETE CASCADE
);
CREATE TABLE 

-- Inserer quelques donnees de test
INSERT IGNORE INTO region (id, libelle) VALUES (1, 'Analamanga');
INSERT IGNORE INTO district (id, libelle, id_region) VALUES (1, 'Antananarivo', 1);
INSERT IGNORE INTO commune (id, libelle, id_district) VALUES (1, 'Antananarivo', 1), (2, 'Avaradrano', 1);
INSERT IGNORE INTO statut (id, libelle) VALUES (1, 'En attente'), (2, 'En cours'), (3, 'Approuvee'), (4, 'Rejetee');
INSERT IGNORE INTO client (id, nom, mdp, contact, adresse) VALUES (1, 'Jean', 'pass123', '+261341234567', 'Rue A, Tana');
