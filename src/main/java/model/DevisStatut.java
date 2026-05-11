package model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "devis_statut")
public class DevisStatut {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_statut", nullable = false)
	private Status statut;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_devis", nullable = false)
	private Devis devis;

	@Column(name = "description", length = 500)
	private String description;

	@Column(name = "date", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date date;

	public DevisStatut() {}

	public DevisStatut(Status statut, Devis devis, String description, Date date) {
		this.statut = statut;
		this.devis = devis;
		this.description = description;
		this.date = date;
	}

	public Long getId() { return id; }
	public Status getStatut() { return statut; }
	public Devis getDevis() { return devis; }
	public String getDescription() { return description; }
	public Date getDate() { return date; }

	public void setStatut(Status statut) { this.statut = statut; }
	public void setDevis(Devis devis) { this.devis = devis; }
	public void setDescription(String description) { this.description = description; }
	public void setDate(Date date) { this.date = date; }
}
