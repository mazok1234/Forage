USE forage;

-- Désactiver temporairement les vérifications des clés étrangères
SET FOREIGN_KEY_CHECKS = 0;

-- Supprimer toutes les données
TRUNCATE TABLE devis_details;
TRUNCATE TABLE devis;
TRUNCATE TABLE demande_statut;
TRUNCATE TABLE demande;
TRUNCATE TABLE type_devis;
TRUNCATE TABLE statut;
TRUNCATE TABLE commune;
TRUNCATE TABLE district;
TRUNCATE TABLE region;
TRUNCATE TABLE client;
TRUNCATE TABLE parametres;

-- Réactiver les vérifications des clés étrangères
SET FOREIGN_KEY_CHECKS = 1;