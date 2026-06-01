package model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "devis_details")
public class DevisDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_devis", nullable = false)
	private Devis devis;

	@Column(name = "libelle", nullable = false, length = 255)
	private String libelle;

	@Column(name = "quantite", nullable = false)
	private Long quantite;

	@Column(name = "prix_unitaire", nullable = false, precision = 10, scale = 2)
	private BigDecimal prixUnitaire;

	public DevisDetails() {}

	public DevisDetails(Devis devis, String libelle, Long quantite, BigDecimal prixUnitaire) {
		this.devis = devis;
		this.libelle = libelle;
		this.quantite = quantite;
		this.prixUnitaire = prixUnitaire;
	}

	public Long getId() { return id; }
	public Devis getDevis() { return devis; }
	public String getLibelle() { return libelle; }
	public Long getQuantite() { return quantite; }
	public BigDecimal getPrixUnitaire() { return prixUnitaire; }

	public void setDevis(Devis devis) { this.devis = devis; }
	public void setLibelle(String libelle) { this.libelle = libelle; }
	public void setQuantite(Long quantite) { this.quantite = quantite; }
	public void setPrixUnitaire(BigDecimal prixUnitaire) { this.prixUnitaire = prixUnitaire; }
}
