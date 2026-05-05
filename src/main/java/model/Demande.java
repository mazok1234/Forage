package model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonBackReference;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "demande")
public class Demande {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_client", nullable = false)
	private Client client;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_commune", nullable = false)
	@JsonManagedReference("commune-demandes")
	private Commune commune;

	@OneToMany(mappedBy = "demande", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<DemandeStatut> demandeStatuts = new ArrayList<>();

	private String lieu;

	public Demande() {}

	public Demande(Client client, Commune commune) {
		this.client = client;
		this.commune = commune;
	}

	public Long getId() { return id; }
	public Client getClient() { return client; }
	public Commune getCommune() { return commune; }
	public List<DemandeStatut> getDemandeStatuts() { return demandeStatuts; }
	public String getLieu() { return lieu; }

	public void setClient(Client client) { this.client = client; }
	public void setCommune(Commune commune) { this.commune = commune; }
	public void setDemandeStatuts(List<DemandeStatut> demandeStatuts) { this.demandeStatuts = demandeStatuts; }
	public void setLieu(String lieu) { this.lieu = lieu; }
}