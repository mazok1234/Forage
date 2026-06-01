package model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "demande_statut")
public class DemandeStatut {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_statut", nullable = false)
	private Status statut;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_demande", nullable = false)
	private Demande demande;

	@Column(name = "description", length = 500)
	private String description;

	@Column(name = "duree_travail")
	private Integer dureeTravail;

	@Column(name = "date", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date date;

	public DemandeStatut() {}

	public DemandeStatut(Status statut, Demande demande, String description, Date date) {
		this.statut = statut;
		this.demande = demande;
		this.description = description;
		this.date = date;
		this.dureeTravail = 0;
	}

	public Long getId() { return id; }
	public Status getStatut() { return statut; }
	public Demande getDemande() { return demande; }
	public String getDescription() { return description; }
	public Integer getDureeTravail() { return dureeTravail; }
	public Date getDate() { return date; }

	public void setStatut(Status statut) { this.statut = statut; }
	public void setDemande(Demande demande) { this.demande = demande; }
	public void setDescription(String description) { this.description = description; }
	public void setDureeTravail(Integer dureeTravail) { this.dureeTravail = dureeTravail; }
	public void setDate(Date date) { this.date = date; }
}