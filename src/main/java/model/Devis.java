package model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "devis")
public class Devis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_demande", nullable = false)
    private Demande demande;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_type_devis", nullable = false)
    private TypeDevis typeDevis;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private Date createdAt;

    @OneToMany(mappedBy = "devis", cascade = CascadeType.ALL)
    private List<DevisDetails> devisDetails = new ArrayList<>();

    @OneToMany(mappedBy = "devis")
    private List<DevisStatut> devisStatuts = new ArrayList<>();

    public Devis() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Demande getDemande() {
        return demande;
    }

    public void setDemande(Demande demande) {
        this.demande = demande;
    }

    public TypeDevis getTypeDevis() {
        return typeDevis;
    }

    public void setTypeDevis(TypeDevis typeDevis) {
        this.typeDevis = typeDevis;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public List<DevisDetails> getDevisDetails() {
        return devisDetails;
    }

    public void setDevisDetails(List<DevisDetails> devisDetails) {
        this.devisDetails = devisDetails;
    }

    public List<DevisStatut> getDevisStatuts() {
        return devisStatuts;
    }

    public void setDevisStatuts(List<DevisStatut> devisStatuts) {
        this.devisStatuts = devisStatuts;
    }
}