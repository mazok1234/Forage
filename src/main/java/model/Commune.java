package model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonBackReference;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "commune")
public class Commune {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(name = "libelle", nullable = false)
	private String libelle;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_district", nullable = false)
	@JsonBackReference("district-communes")
	private District district;

	@OneToMany(mappedBy = "commune")
	@JsonBackReference("commune-demandes")
	private List<Demande> demandes = new ArrayList<>();

	public Commune() {}

	public Commune(String libelle, District district) {
		this.libelle = libelle;
		this.district = district;
	}

	public Long getId() {return id;}
	public String getLibelle() {return libelle;}
	public District getDistrict() {return district;}
	public List<Demande> getDemandes() {return demandes;}

	public void setLibelle(String libelle) {this.libelle = libelle;}
	public void setDistrict(District district) {this.district = district;}
	public void setDemandes(List<Demande> demandes) {this.demandes = demandes;}
}
