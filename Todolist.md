# Objectif: Creer un site spring boot sur le forage

---

* 1 - Conception du database:
 - Creer un database forage_db
    - Client : id, nom, mdp,contact, adresse 
    - Region : id, Libelle
    - District : id, Libelle, idDistrict
    - Commune : id, Libelle , idCommune
    - Demande : id, idClient, idCommune 
    - Statut : id, libelle
    - DemandeStatut : id, idStatut, idDemande, description, date

* 2 - Creation des elements Spring 
  - Creation des Entity : Client, region, dstrict, commune, demmande , statut , demandeStatut
  - Creation des repository pour chaque entity
  - Creation des services
  - Creation des controller :
    - mapping + appel de service

* 3 - Creer un page accueil :
    - Ajouter un selection de Region -> District -> Commune
    - Ajouter un zone de texte pour le demande

    