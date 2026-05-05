package model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "statut")
public class Status {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(name = "libelle", nullable = false, length = 255)
	private String libelle;

	@OneToMany(mappedBy = "statut", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<DemandeStatut> demandeStatuts = new ArrayList<>();

	public Status() {}

	public Status(String libelle) {
		this.libelle = libelle;
	}

	public Long getId() { return id; }
	public String getLibelle() { return libelle; }
	public List<DemandeStatut> getDemandeStatuts() { return demandeStatuts; }

	public void setLibelle(String libelle) { this.libelle = libelle; }
	public void setDemandeStatuts(List<DemandeStatut> demandeStatuts) { this.demandeStatuts = demandeStatuts; }
}
