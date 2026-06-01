package model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "type_devis")
public class TypeDevis {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(name = "libelle", nullable = false, length = 255)
	private String libelle;

	@OneToMany(mappedBy = "typeDevis", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Devis> devis = new ArrayList<>();

	public TypeDevis() {}

	public TypeDevis(String libelle) {
		this.libelle = libelle;
	}

	public Long getId() { return id; }
	public String getLibelle() { return libelle; }
	public List<Devis> getDevis() { return devis; }

	public void setLibelle(String libelle) { this.libelle = libelle; }
	public void setDevis(List<Devis> devis) { this.devis = devis; }
}
