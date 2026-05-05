package model;

import java.util.Date;
import jakarta.persistence.*;
import java.util.List;

@Entity
public class Demande {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String district;
    private String commune;
    private String fokontany;
    private Date date_demande;
    private String personne_qui_demande;

    @OneToMany(mappedBy = "demande")
    private List<StatusDemande> statusDemandes;

    // getters & setters
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getDistrict() {
        return district;
    }
    public void setDistrict(String district) {
        this.district = district;
    }
    public String getCommune() {
        return commune;
    }
    public void setCommune(String commune) {
        this.commune = commune;
    }
    public String getFokontany() {
        return fokontany;
    }
    public void setFokontany(String fokontany) {
        this.fokontany = fokontany;
    }
    public Date getDateDemande() {
        return date_demande;
    }
    public void setDateDemande(Date date_demande) {
        this.date_demande = date_demande;
    }
    public String getPersonneQuiDemande() {
        return personne_qui_demande;
    }
    public void setPersonneQuiDemande(String personne_qui_demande) {
        this.personne_qui_demande = personne_qui_demande;
    }
    public List<StatusDemande> getStatusDemandes() {
        return statusDemandes;
    }
    public void setStatusDemandes(List<StatusDemande> statusDemandes) {
        this.statusDemandes = statusDemandes;
    }
    
}