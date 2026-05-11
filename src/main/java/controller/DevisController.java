package controller;

import model.*;
import service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/devis")
public class DevisController {

	private static final Long STATUT_EN_ATTENTE_ID = 1L;
	private static final Long STATUT_APPROUVEE_ID = 2L;
	private static final Long STATUT_REJETEE_ID = 3L;
	private static final String STATUT_DEMANDE_APPROUVEE_LABEL = "Approuvee";

	@Autowired
	private DevisService devisService;
	@Autowired
	private DemandeService demandeService;
	@Autowired
	private StatusService statusService;
	@Autowired
	private DevisStatutService devisStatutService;

	@GetMapping("/demandes")
	public String listDemandesApprouvees(Model model) {
		List<Demande> demandes = demandeService.getAllDemandes();
		Map<Long, DemandeStatut> latestStatuts = getLatestStatuts(demandes);
		List<Demande> approuvees = demandes.stream()
			.filter(demande -> isDemandeApprouvee(latestStatuts.get(demande.getId())))
			.toList();

		model.addAttribute("demandes", approuvees);
		model.addAttribute("latestStatuts", latestStatuts);
		return "devis/demandes";
	}

	@GetMapping("/create/{demandeId}")
	public String showCreateForm(@PathVariable("demandeId") Long demandeId, Model model) {
		Demande demande = demandeService.getDemandeByIdWithDetails(demandeId).orElse(null);
		if (demande == null) {
			return "redirect:/devis/demandes";
		}
		model.addAttribute("demande", demande);
		return "devis/form";
	}

	@PostMapping("/create")
	public String createDevis(
			@RequestParam("demandeId") Long demandeId,
			@RequestParam("libelle") String libelle,
			@RequestParam("montant") BigDecimal montant,
			Model model) {
		Demande demande = demandeService.getDemandeByIdWithDetails(demandeId).orElse(null);
		if (demande == null) {
			return "redirect:/devis/demandes";
		}

		String trimmedLibelle = libelle == null ? "" : libelle.trim();
		if (trimmedLibelle.isEmpty()) {
			model.addAttribute("error", "Libelle requis");
			model.addAttribute("demande", demande);
			return "devis/form";
		}
		if (montant == null || montant.compareTo(BigDecimal.ZERO) <= 0) {
			model.addAttribute("error", "Montant invalide");
			model.addAttribute("demande", demande);
			return "devis/form";
		}

		Status statut = statusService.getStatusById(STATUT_EN_ATTENTE_ID).orElse(null);
		if (statut == null) {
			model.addAttribute("error", "Statut 'En attente' introuvable");
			model.addAttribute("demande", demande);
			return "devis/form";
		}

		Devis devis = new Devis(demande, trimmedLibelle, montant, new Date());
		Devis savedDevis = devisService.createDevis(devis);

		DevisStatut devisStatut = new DevisStatut(
			statut,
			savedDevis,
			"Creation du devis",
			new Date()
		);
		devisStatutService.createDevisStatut(devisStatut);
		return "redirect:/devis/list?created=true";
	}

	@GetMapping("/list")
	public String listDevis(@RequestParam(value = "created", required = false) Boolean created, Model model) {
		List<Devis> devis = devisService.getAllDevis();
		model.addAttribute("devisList", devis);
		model.addAttribute("latestDevisStatuts", getLatestDevisStatuts(devis));
		if (Boolean.TRUE.equals(created)) {
			model.addAttribute("message", "Devis cree avec succes");
		}
		return "devis/list";
	}

	@PostMapping("/accept/{id}")
	public String acceptDevis(@PathVariable("id") Long id) {
		Devis devis = devisService.getDevisByIdWithDetails(id).orElse(null);
		if (devis == null) {
			return "redirect:/devis/list";
		}

		Status statut = statusService.getStatusById(STATUT_APPROUVEE_ID).orElse(null);
		if (statut == null) {
			return "redirect:/devis/list";
		}
		DevisStatut devisStatut = new DevisStatut(
			statut,
			devis,
			"Devis approuve",
			new Date()
		);
		devisStatutService.createDevisStatut(devisStatut);

		return "redirect:/devis/list";
	}

	@PostMapping("/refuse/{id}")
	public String refuseDevis(@PathVariable("id") Long id) {
		Devis devis = devisService.getDevisByIdWithDetails(id).orElse(null);
		if (devis == null) {
			return "redirect:/devis/list";
		}

		Status statut = statusService.getStatusById(STATUT_REJETEE_ID).orElse(null);
		if (statut == null) {
			return "redirect:/devis/list";
		}
		DevisStatut devisStatut = new DevisStatut(
			statut,
			devis,
			"Devis rejete",
			new Date()
		);
		devisStatutService.createDevisStatut(devisStatut);
		return "redirect:/devis/list";
	}

	private boolean isDemandeApprouvee(DemandeStatut statutInfo) {
		if (statutInfo == null || statutInfo.getStatut() == null) {
			return false;
		}
		Status statut = statutInfo.getStatut();
		if (statut.getId() != null && statut.getId().equals(STATUT_APPROUVEE_ID)) {
			return true;
		}
		return STATUT_DEMANDE_APPROUVEE_LABEL.equalsIgnoreCase(statut.getLibelle());
	}

	private Map<Long, DemandeStatut> getLatestStatuts(List<Demande> demandes) {
		Map<Long, DemandeStatut> latestStatuts = new HashMap<>();
		for (Demande demande : demandes) {
			DemandeStatut latest = demande.getDemandeStatuts()
				.stream()
				.max(Comparator.comparing(DemandeStatut::getDate)
					.thenComparing(DemandeStatut::getId))
				.orElse(null);
			if (latest != null) {
				latestStatuts.put(demande.getId(), latest);
			}
		}
		return latestStatuts;
	}

	private Map<Long, DevisStatut> getLatestDevisStatuts(List<Devis> devisList) {
		Map<Long, DevisStatut> latestStatuts = new HashMap<>();
		for (Devis devis : devisList) {
			DevisStatut latest = devis.getDevisStatuts()
				.stream()
				.max(Comparator.comparing(DevisStatut::getDate)
					.thenComparing(DevisStatut::getId))
				.orElse(null);
			if (latest != null) {
				latestStatuts.put(devis.getId(), latest);
			}
		}
		return latestStatuts;
	}
}
