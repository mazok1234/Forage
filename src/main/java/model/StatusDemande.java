package model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
public class StatusDemande {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "demande_id")
    private Demande demande;

    @ManyToOne
    @JoinColumn(name = "status_id")
    private Status status;

    private Date date_status;

    // getters & setters
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public Demande getDemande() {
        return demande;
    }
    public void setDemande(Demande demande) {
        this.demande = demande;
    }
    public Status getStatus() {
        return status;
    }
    public void setStatus(Status status) {
        this.status = status;
    }
    public Date getDateStatus() {
        return date_status;
    }
    public void setDateStatus(Date date_status) {
        this.date_status = date_status;
    }
}