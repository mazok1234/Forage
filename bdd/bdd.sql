CREATE DATABASE forage;
USE forage;
CREATE TABLE Demande(
    id INT PRIMARY KEY AUTO_INCREMENT,
    district VARCHAR(255) NOT NULL,
    commune VARCHAR(255) NOT NULL,
    fokontany VARCHAR(255) NOT NULL,
    date_demande DATE NOT NULL,
    personne_qui_demande VARCHAR(255) NOT NULL
);
CREATE TABLE Status(
    id INT PRIMARY KEY AUTO_INCREMENT,
    description VARCHAR(255) NOT NULL
);
CREATE TABLE StatusDemande(
    id INT PRIMARY KEY AUTO_INCREMENT,
    demande_id INT NOT NULL,
    status_id INT NOT NULL,
    date_status DATE NOT NULL,
    FOREIGN KEY (demande_id) REFERENCES Demande(id),
    FOREIGN KEY (status_id) REFERENCES Status(id)
);