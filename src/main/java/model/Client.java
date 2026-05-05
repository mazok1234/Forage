package model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "client")
public class Client {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(name = "nom", nullable = false, length = 255)
	private String nom;

	@Column(name = "mdp", nullable = false, length = 255)
	private String mdp;

	@Column(name = "contact", length = 20)
	private String contact;

	@Column(name = "adresse", length = 500)
	private String adresse;

	@OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Demande> demandes = new ArrayList<>();

	public Client() {}

	public Client(String nom, String mdp, String contact, String adresse) {
		this.nom = nom;
		this.mdp = mdp;
		this.contact = contact;
		this.adresse = adresse;
	}

	public Long getId() { return id; }
	public String getNom() { return nom; }
	public String getMdp() { return mdp; }
	public String getContact() { return contact; }
	public String getAdresse() { return adresse; }
	public List<Demande> getDemandes() { return demandes; }

	public void setNom(String nom) { this.nom = nom; }
	public void setMdp(String mdp) { this.mdp = mdp; }
	public void setContact(String contact) { this.contact = contact; }
	public void setAdresse(String adresse) { this.adresse = adresse; }
	public void setDemandes(List<Demande> demandes) { this.demandes = demandes; }
}
