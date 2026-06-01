package controller;

import model.*;
import service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/devis")
public class DevisController {

	private static final Long STATUT_DEVIS_ETUDE_ID = 4L;
	private static final Long STATUT_DEVIS_FORAGE_ID = 5L;
	private static final Long STATUT_DEMANDE_ETUDE_ID = 2L;
	private static final Long STATUT_DEMANDE_FORAGE_ID = 3L;
	private static final Long STATUT_APPROUVEE_ID = 2L;
	private static final Long STATUT_REJETEE_ID = 3L;

	@Autowired
	private DevisService devisService;
	@Autowired
	private DemandeService demandeService;
	@Autowired
	private StatusService statusService;
	@Autowired
	private DevisStatutService devisStatutService;
	@Autowired
	private DemandeStatutService demandeStatutService;
	@Autowired
	private DevisDetailsService devisDetailsService;
	@Autowired
	private TypeDevisService typeDevisService;

	@PostMapping("/create")
	public String createDevis(
			@RequestParam("reference") String reference,
			@RequestParam("typeDevisId") Long typeDevisId,
			@RequestParam("detailLibelle") List<String> detailLibelles,
			@RequestParam("detailQuantite") List<Long> detailQuantites,
			@RequestParam("detailPrixUnitaire") List<BigDecimal> detailPrixUnitaires,
			@RequestParam(value = "statutDate", required = false) String statutDate,
			@RequestParam(value = "statutTime", required = false) String statutTime,
			Model model) {
		String trimmedReference = reference == null ? "" : reference.trim();
		Demande demande = demandeService.getDemandeByReferenceWithDetails(trimmedReference).orElse(null);
		if (demande == null) {
			model.addAttribute("error", "Demande introuvable");
			populateCreateModel(model);
			return "devis/create";
		}

		TypeDevis typeDevis = typeDevisService.getTypeDevisById(typeDevisId).orElse(null);
		if (typeDevis == null) {
			model.addAttribute("error", "Type devis invalide");
			populateCreateModel(model);
			return "devis/create";
		}

		Long devisStatutId = typeDevis.getId() != null && typeDevis.getId() == 1L
			? STATUT_DEVIS_ETUDE_ID
			: STATUT_DEVIS_FORAGE_ID;
		Status statut = statusService.getStatusById(devisStatutId).orElse(null);
		if (statut == null) {
			model.addAttribute("error", "Statut devis introuvable");
			populateCreateModel(model);
			return "devis/create";
		}

		if (detailLibelles == null || detailQuantites == null || detailPrixUnitaires == null) {
			model.addAttribute("error", "Ajoutez au moins une ligne de details");
			populateCreateModel(model);
			return "devis/create";
		}

		int max = Math.max(detailLibelles.size(), Math.max(detailQuantites.size(), detailPrixUnitaires.size()));
		List<DevisDetails> detailsList = new ArrayList<>();
		for (int i = 0; i < max; i++) {
			String libelle = i < detailLibelles.size() ? detailLibelles.get(i) : null;
			Long quantite = i < detailQuantites.size() ? detailQuantites.get(i) : null;
			BigDecimal prixUnitaire = i < detailPrixUnitaires.size() ? detailPrixUnitaires.get(i) : null;

			String trimmedLibelle = libelle == null ? "" : libelle.trim();
			if (trimmedLibelle.isEmpty() && quantite == null && prixUnitaire == null) {
				continue;
			}
			if (trimmedLibelle.isEmpty() || quantite == null || quantite <= 0 || prixUnitaire == null || prixUnitaire.compareTo(BigDecimal.ZERO) <= 0) {
				model.addAttribute("error", "Details invalides");
				populateCreateModel(model);
				return "devis/create";
			}

			DevisDetails details = new DevisDetails(null, trimmedLibelle, quantite, prixUnitaire);
			detailsList.add(details);
		}

		if (detailsList.isEmpty()) {
			model.addAttribute("error", "Ajoutez au moins une ligne de details");
			populateCreateModel(model);
			return "devis/create";
		}

		Date statutDateValue = resolveStatutDate(statutDate, statutTime);
		Devis devis = new Devis();
		devis.setDemande(demande);
		devis.setTypeDevis(typeDevis);
		devis.setCreatedAt(statutDateValue);
		Devis savedDevis = devisService.createDevis(devis);

		for (DevisDetails details : detailsList) {
			details.setDevis(savedDevis);
			devisDetailsService.createDevisDetails(details);
		}

		DevisStatut devisStatut = new DevisStatut(
			statut,
			savedDevis,
			"Creation du devis",
			statutDateValue
		);
		devisStatutService.createDevisStatut(devisStatut);

		Long demandeStatutId = typeDevis.getId() != null && typeDevis.getId() == 1L
			? STATUT_DEMANDE_ETUDE_ID
			: STATUT_DEMANDE_FORAGE_ID;
		String demandeStatutDescription = typeDevis.getId() != null && typeDevis.getId() == 1L
			? "Demande etude cree"
			: "Demande forage cree";
		statusService.getStatusById(demandeStatutId).ifPresent(status -> {
			demandeStatutService.createDemandeStatutWithDuration(
				status,
				demande,
				demandeStatutDescription,
				statutDateValue
			);
		});
		return "redirect:/devis/create?created=true";
	}

	@GetMapping("/create")
	public String listDevis(@RequestParam(value = "created", required = false) Boolean created, Model model) {
		populateCreateModel(model);
		if (Boolean.TRUE.equals(created)) {
			model.addAttribute("message", "Devis cree avec succes");
		}
		return "devis/create";
	}

	@GetMapping("/demande-info")
	@ResponseBody
	public ResponseEntity<Map<String, String>> getDemandeInfo(@RequestParam("reference") String reference) {
		String trimmedReference = reference == null ? "" : reference.trim();
		return demandeService.getDemandeByReferenceWithDetails(trimmedReference)
			.map(demande -> {
				Map<String, String> payload = new HashMap<>();
				payload.put("reference", safe(demande.getReference()));
				payload.put("clientNom", safe(demande.getClient().getNom()));
				payload.put("clientContact", safe(demande.getClient().getContact()));
				payload.put("clientAdresse", safe(demande.getClient().getAdresse()));
				payload.put("region", safe(demande.getCommune().getDistrict().getRegion().getLibelle()));
				payload.put("district", safe(demande.getCommune().getDistrict().getLibelle()));
				payload.put("commune", safe(demande.getCommune().getLibelle()));
				payload.put("lieu", safe(demande.getLieu()));
				return ResponseEntity.ok(payload);
			})
			.orElse(ResponseEntity.notFound().build());
	}

	private String safe(String value) {
		return value == null ? "" : value;
	}

	private void populateCreateModel(Model model) {
		model.addAttribute("typesDevis", typeDevisService.getAllTypeDevis());
		model.addAttribute("demandes", demandeService.getAllDemandes());
		model.addAttribute("defaultDate", LocalDate.now().toString());
		model.addAttribute("defaultTime", LocalTime.now().withSecond(0).withNano(0)
			.format(DateTimeFormatter.ofPattern("HH:mm")));
	}

	private Date resolveStatutDate(String dateValue, String timeValue) {
		try {
			boolean emptyDate = dateValue == null || dateValue.isBlank();
			boolean emptyTime = timeValue == null || timeValue.isBlank();
			if (emptyDate && emptyTime) {
				return new Date();
			}
			LocalDate date = emptyDate ? LocalDate.now() : LocalDate.parse(dateValue);
			LocalTime time = emptyTime ? LocalTime.now().withSecond(0).withNano(0) : LocalTime.parse(timeValue);
			LocalDateTime dateTime = LocalDateTime.of(date, time);
			return Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
		} catch (Exception ex) {
			return new Date();
		}
	}

	@PostMapping("/accept/{id}")
	public String acceptDevis(@PathVariable("id") Long id) {
		Devis devis = devisService.getDevisByIdWithDetails(id).orElse(null);
		if (devis == null) {
			return "redirect:/devis/create";
		}

		Status statut = statusService.getStatusById(STATUT_APPROUVEE_ID).orElse(null);
		if (statut == null) {
			return "redirect:/devis/create";
		}
		DevisStatut devisStatut = new DevisStatut(
			statut,
			devis,
			"Devis approuve",
			new Date()
		);
		devisStatutService.createDevisStatut(devisStatut);

		return "redirect:/devis/create";
	}

	@PostMapping("/refuse/{id}")
	public String refuseDevis(@PathVariable("id") Long id) {
		Devis devis = devisService.getDevisByIdWithDetails(id).orElse(null);
		if (devis == null) {
			return "redirect:/devis/create";
		}

		Status statut = statusService.getStatusById(STATUT_REJETEE_ID).orElse(null);
		if (statut == null) {
			return "redirect:/devis/create";
		}
		DevisStatut devisStatut = new DevisStatut(
			statut,
			devis,
			"Devis rejete",
			new Date()
		);
		devisStatutService.createDevisStatut(devisStatut);
		return "redirect:/devis/create";
	}

}
