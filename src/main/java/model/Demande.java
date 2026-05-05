package model;

import jakarta.persistence.*;
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
	private Commune commune;

	@OneToMany(mappedBy = "demande", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<DemandeStatut> demandeStatuts = new ArrayList<>();

	public Demande() {}

	public Demande(Client client, Commune commune) {
		this.client = client;
		this.commune = commune;
	}

	public Long getId() { return id; }
	public Client getClient() { return client; }
	public Commune getCommune() { return commune; }
	public List<DemandeStatut> getDemandeStatuts() { return demandeStatuts; }

	public void setClient(Client client) { this.client = client; }
	public void setCommune(Commune commune) { this.commune = commune; }
	public void setDemandeStatuts(List<DemandeStatut> demandeStatuts) { this.demandeStatuts = demandeStatuts; }
}