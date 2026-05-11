package model;

import jakarta.persistence.*;
import java.math.BigDecimal;
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

	@Column(name = "libelle", nullable = false, length = 255)
	private String libelle;

	@Column(name = "montant", nullable = false, precision = 10, scale = 2)
	private BigDecimal montant;

	@OneToMany(mappedBy = "devis", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<DevisStatut> devisStatuts = new ArrayList<>();

	@Column(name = "date", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date date;

	public Devis() {}

	public Devis(Demande demande, String libelle, BigDecimal montant, Date date) {
		this.demande = demande;
		this.libelle = libelle;
		this.montant = montant;
		this.date = date;
	}

	public Long getId() { return id; }
	public Demande getDemande() { return demande; }
	public String getLibelle() { return libelle; }
	public BigDecimal getMontant() { return montant; }
	public Date getDate() { return date; }
	public List<DevisStatut> getDevisStatuts() { return devisStatuts; }

	public void setDemande(Demande demande) { this.demande = demande; }
	public void setLibelle(String libelle) { this.libelle = libelle; }
	public void setMontant(BigDecimal montant) { this.montant = montant; }
	public void setDate(Date date) { this.date = date; }
	public void setDevisStatuts(List<DevisStatut> devisStatuts) { this.devisStatuts = devisStatuts; }
}
