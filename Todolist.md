# 📋 Todo List — Projet Demande de Forage (Spring MVC pur)

## 🗄️ Base de données (MySQL)

### Création des tables
- [ ] Table `Demande` : `id`, `district`, `commune`, `fokontany`, `date`, `personne_qui_demande`, `reference`
- [ ] Table `Statut` : `id`, `description`
- [ ] Table `Demande_Statut` : `id`, `id_demande`, `id_statut`, `date`

---

## 🔧 Configuration du projet

### Structure du projet (Maven WAR)
- [ ] Créer un projet Maven de type `war`
- [ ] Configurer `pom.xml` avec les dépendances :
  - [ ] `spring-webmvc`
  - [ ] `spring-orm`
  - [ ] `hibernate-core`
  - [ ] `mysql-connector-java`
  - [ ] `javax.servlet-api`
  - [ ] `jstl`

### Configuration XML / Java
- [ ] `web.xml` — déclarer le `DispatcherServlet`
- [ ] `spring-mvc.xml` — configurer le ViewResolver (JSP), component-scan
- [ ] `spring-db.xml` (ou `applicationContext.xml`) — configurer :
  - [ ] `DataSource` (connexion MySQL)
  - [ ] `SessionFactory` Hibernate
  - [ ] `HibernateTransactionManager`

---

## ⚙️ Backend — Spring MVC

### Couche Model
- [ ] Créer l'entité `Demande` (annotations Hibernate : `@Entity`, `@Table`, `@Id`...)
- [ ] Créer l'entité `Statut`
- [ ] Créer l'entité `DemandeStatut`
- [ ] Configurer les relations Hibernate (`@OneToMany`, `@ManyToOne`)

### Couche DAO (pas Repository)
- [ ] Interface `DemandeDao`
- [ ] `DemandeDaoImpl` — utiliser `SessionFactory` (Hibernate)
- [ ] Interface `StatutDao`
- [ ] `StatutDaoImpl`
- [ ] Interface `DemandeStatutDao`
- [ ] `DemandeStatutDaoImpl`

### Couche Service
- [ ] Interface `DemandeService`
- [ ] `DemandeServiceImpl` — logique métier + `@Transactional`

### Couche Controller
- [ ] `DemandeController` avec les routes :
  - [ ] `GET /demandes` → afficher la liste
  - [ ] `GET /demandes/new` → afficher le formulaire
  - [ ] `POST /demandes` → enregistrer une nouvelle demande

---

## 🎨 Frontend — Vues JSP

### Formulaire de création (`new.jsp`)
- [ ] Champ : District
- [ ] Champ : Commune
- [ ] Champ : Fokontany
- [ ] Champ : Personne qui demande
- [ ] Champ : Référence
- [ ] Champ : Date
- [ ] Validation des champs obligatoires
- [ ] Message de confirmation après soumission

### Liste des demandes (`list.jsp`)
- [ ] Tableau affichant toutes les demandes
- [ ] Colonne statut actuel de chaque demande

---

## 🚀 Déploiement
- [ ] Générer le fichier `WAR`
- [ ] Déployer sur **Tomcat** (pas de serveur embarqué)