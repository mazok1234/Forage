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
    reference VARCHAR(255) NOT NULL,
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
    duree_travail INT,
    description VARCHAR(500),
    date TIMESTAMP NOT NULL,
    FOREIGN KEY (id_statut) REFERENCES statut(id) ON DELETE CASCADE,
    FOREIGN KEY (id_demande) REFERENCES demande(id) ON DELETE CASCADE
);
CREATE TABLE IF NOT EXISTS devis (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    id_demande BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    id_type_devis BIGINT NOT NULL,
    FOREIGN KEY (id_demande) REFERENCES demande(id) ON DELETE CASCADE,
    FOREIGN KEY (id_type_devis) REFERENCES type_devis(id) ON DELETE CASCADE
);
CREATE TABLE IF NOT EXISTS type_devis (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    libelle VARCHAR(255) NOT NULL
);
CREATE TABLE IF NOT EXISTS devis_details (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    id_devis BIGINT NOT NULL,
    libelle VARCHAR(255) NOT NULL,
    quantite BIGINT NOT NULL,
    prix_unitaire DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (id_devis) REFERENCES devis(id) ON DELETE CASCADE
);
CREATE TABLE IF NOT EXISTS parametres(
    idStatut1 BIGINT NOT NULL,
    idStatut2 BIGINT NOT NULL,
    duree_travail INT NOT NULL,
    alerte_couleur VARCHAR(200) NOT NULL
);


-- Inserer quelques donnees de test
INSERT IGNORE INTO region (id, libelle) VALUES (1, 'Analamanga');
INSERT IGNORE INTO district (id, libelle, id_region) VALUES (1, 'Antananarivo', 1);
INSERT IGNORE INTO commune (id, libelle, id_district) VALUES (1, 'Antananarivo', 1), (2, 'Avaradrano', 1);
INSERT IGNORE INTO statut (id, libelle) VALUES (1, 'Cree'), (2, 'Demande_etude_cree'), (3, 'Demande_forage_cree'), (4, 'Devis_etude_cree'), (5, 'Devis_forage_cree'),(6, "Terminee");
INSERT IGNORE INTO type_devis (id, libelle) VALUES (1, 'Devis_etude'), (2, 'Devis_forage');
INSERT IGNORE INTO client (id, nom, mdp, contact, adresse) VALUES (1, 'Jean', 'pass123', '+261341234567', 'Rue A, Tana');

INSERT IGNORE INTO parametres (idStatut1, idStatut2, duree_travail, alerte_couleur) VALUES (1, 2, 10, 'red'), (2, 3, 72, 'orange'), (3, 4, 96, 'yellow'), (4, 5, 120, 'blue'), (5, 6, 144, 'green');

