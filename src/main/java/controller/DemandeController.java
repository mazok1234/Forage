package controller;

import model.*;
import service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Controller
@RequestMapping("/demande")
public class DemandeController {

	private record OptionDto(Long id, String libelle) {}
	private static final Long STATUT_DEMANDE_CREE_ID = 1L;

	@Autowired
	private DemandeService demandeService;
	@Autowired
	private ClientService clientService;
	@Autowired
	private RegionService regionService;
	@Autowired
	private DistrictService districtService;
	@Autowired
	private CommuneService communeService;
	@Autowired
	private StatusService statusService;
	@Autowired
	private DemandeStatutService demandeStatutService;

	@GetMapping("/")
	public String showForm(Model model) {
		Demande demande = new Demande();
		populateFormModel(model, demande);
		model.addAttribute("formAction", "/demande/create");
		model.addAttribute("submitLabel", "Creer la Demande");
		model.addAttribute("formTitle", "Creer une Nouvelle Demande");
		model.addAttribute("pageTitle", "Nouvelle Demande");
		return "demande/form";
	}

	@GetMapping("/list")
	public String listDemandes(@RequestParam(value = "created", required = false) Boolean created, Model model) {
		List<Demande> demandes = demandeService.getAllDemandes();
		model.addAttribute("demandes", demandes);
		model.addAttribute("latestStatuts", getLatestStatuts(demandes));
		model.addAttribute("statuses", statusService.getAllStatuses());
		if (Boolean.TRUE.equals(created)) {
			model.addAttribute("message", "Demande créée avec succès");
		}
		return "demande/list";
	}

	@GetMapping("/edit/{id}")
	public String editDemande(@PathVariable("id") Long id, Model model) {
		return demandeService.getDemandeByIdWithDetails(id)
			.map(demande -> {
				populateFormModel(model, demande);
				model.addAttribute("formAction", "/demande/update");
				model.addAttribute("submitLabel", "Mettre a jour");
				model.addAttribute("formTitle", "Modifier la Demande");
				model.addAttribute("pageTitle", "Modifier la Demande");
				return "demande/form";
			})
			.orElse("redirect:/demande/list");
	}

	@GetMapping("/districts/{regionId}")
	@ResponseBody
	public ResponseEntity<List<OptionDto>> getDistrictsByRegion(@PathVariable("regionId") Long regionId) {
		return regionService.getRegionById(regionId)
			.map(region -> districtService.getDistrictsByRegion(region)
				.stream()
				.map(district -> new OptionDto(district.getId(), district.getLibelle()))
				.toList())
			.map(ResponseEntity::ok)
			.orElse(ResponseEntity.notFound().build());
	}

	@GetMapping("/communes/{districtId}")
	@ResponseBody
	public ResponseEntity<List<OptionDto>> getCommunesByDistrict(@PathVariable("districtId") Long districtId) {
		return districtService.getDistrictById(districtId)
			.map(district -> communeService.getCommunesByDistrict(district)
				.stream()
				.map(commune -> new OptionDto(commune.getId(), commune.getLibelle()))
				.toList())
			.map(ResponseEntity::ok)
			.orElse(ResponseEntity.notFound().build());
	}

	@PostMapping("/create")
	public String createDemande(
			@RequestParam("clientId") Long clientId,
			@RequestParam("communeId") Long communeId,
			@RequestParam("lieu") String lieu,
			@RequestParam(value = "statutDate", required = false) String statutDate,
			@RequestParam(value = "statutTime", required = false) String statutTime,
			Model model) {
		try {
			Client client = clientService.getClientById(clientId).orElse(null);
			Commune commune = communeService.getCommuneById(communeId).orElse(null);
			
			if (client != null && commune != null) {
				String reference = demandeService.getNextReference();
				Demande demande = new Demande(client, commune, reference);
				demande.setLieu(lieu);
				
				Demande savedDemande = demandeService.createDemande(demande);
				statusService.getStatusById(STATUT_DEMANDE_CREE_ID).ifPresent(status -> {
					Date statutDateValue = resolveStatutDate(statutDate, statutTime);
					demandeStatutService.createDemandeStatutWithDuration(
						status,
						savedDemande,
						"Demande cree",
						statutDateValue
					);
				});
				
				return "redirect:/demande/list?created=true";
			} else {
				model.addAttribute("error", "Client ou Commune non trouvés");
				return showForm(model);
			}
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("error", "Erreur: " + e.getMessage());
			return showForm(model);
		}
	}

	@PostMapping("/update")
	public String updateDemande(
			@RequestParam("id") Long id,
			@RequestParam("clientId") Long clientId,
			@RequestParam("communeId") Long communeId,
			@RequestParam("lieu") String lieu,
			Model model) {
		Demande demande = demandeService.getDemandeById(id).orElse(null);
		if (demande == null) {
			return "redirect:/demande/list";
		}

		Client client = clientService.getClientById(clientId).orElse(null);
		Commune commune = communeService.getCommuneById(communeId).orElse(null);
		if (client == null || commune == null) {
			model.addAttribute("error", "Client ou Commune non trouves");
			return editDemande(id, model);
		}

		demande.setClient(client);
		demande.setCommune(commune);
		demande.setLieu(lieu);
		demandeService.updateDemande(demande);
		return "redirect:/demande/list";
	}

	@PostMapping("/delete/{id}")
	public String deleteDemande(@PathVariable("id") Long id) {
		demandeService.deleteDemande(id);
		return "redirect:/demande/list";
	}

	@PostMapping("/status/{id}")
	public String addStatus(
			@PathVariable("id") Long id,
			@RequestParam("statusId") Long statusId,
			@RequestParam(value = "description", required = false) String description,
			@RequestParam(value = "statutDate", required = false) String statutDate,
			@RequestParam(value = "statutTime", required = false) String statutTime) {
		Demande demande = demandeService.getDemandeById(id).orElse(null);
		Status status = statusService.getStatusById(statusId).orElse(null);
		if (demande == null || status == null) {
			return "redirect:/demande/list";
		}
		Date statutDateValue = resolveStatutDate(statutDate, statutTime);
		demandeStatutService.createDemandeStatutWithDuration(
			status,
			demande,
			description,
			statutDateValue
		);
		return "redirect:/demande/list";
	}

	@GetMapping("/statut")
	public String showStatutForm(
			@RequestParam(value = "created", required = false) Boolean created,
			@RequestParam(value = "updated", required = false) Boolean updated,
			Model model) {
		populateStatutModel(model);
		if (Boolean.TRUE.equals(created)) {
			model.addAttribute("message", "Statut ajoute avec succes");
		} else if (Boolean.TRUE.equals(updated)) {
			model.addAttribute("message", "Statut modifie avec succes");
		}
		return "demande/status";
	}

	@GetMapping("/suivi")
	public String showSuivi(Model model) {
		model.addAttribute("demandes", demandeService.getAllDemandes());
		return "demande/suivi";
	}

	@PostMapping("/statut")
	public String createStatut(
			@RequestParam("reference") String reference,
			@RequestParam("statusId") Long statusId,
			@RequestParam(value = "description", required = false) String description,
			@RequestParam(value = "statutDate", required = false) String statutDate,
			@RequestParam(value = "statutTime", required = false) String statutTime,
			Model model) {
		String trimmedReference = reference == null ? "" : reference.trim();
		Demande demande = demandeService.getDemandeByReferenceWithDetails(trimmedReference).orElse(null);
		Status status = statusService.getStatusById(statusId).orElse(null);
		if (demande == null || status == null) {
			populateStatutModel(model);
			model.addAttribute("error", "Demande ou statut introuvable");
			return "demande/status";
		}
		Date statutDateValue = resolveStatutDate(statutDate, statutTime);
		demandeStatutService.createDemandeStatutWithDuration(
			status,
			demande,
			description,
			statutDateValue
		);
		return "redirect:/demande/statut?created=true";
	}

	@PostMapping("/statut/update")
	public String updateStatut(
			@RequestParam("demandeStatutId") Long demandeStatutId,
			@RequestParam("statusId") Long statusId,
			@RequestParam(value = "description", required = false) String description,
			@RequestParam(value = "statutDate", required = false) String statutDate,
			@RequestParam(value = "statutTime", required = false) String statutTime,
			Model model) {
		DemandeStatut demandeStatut = demandeStatutService.getDemandeStatutById(demandeStatutId).orElse(null);
		Status status = statusService.getStatusById(statusId).orElse(null);
		if (demandeStatut == null || status == null) {
			populateStatutModel(model);
			model.addAttribute("error", "Statut introuvable");
			return "demande/status";
		}
		Date statutDateValue = resolveStatutDate(statutDate, statutTime);
		demandeStatutService.updateDemandeStatutWithDuration(
			demandeStatut,
			status,
			description,
			statutDateValue
		);
		return "redirect:/demande/statut?updated=true";
	}

	@GetMapping("/info")
	@ResponseBody
	public ResponseEntity<Map<String, String>> getDemandeInfo(@RequestParam("reference") String reference) {
		String trimmedReference = reference == null ? "" : reference.trim();
		return demandeService.getDemandeByReferenceWithDetails(trimmedReference)
			.map(demande -> ResponseEntity.ok(buildDemandeInfo(demande)))
			.orElse(ResponseEntity.notFound().build());
	}

	private void populateFormModel(Model model, Demande demande) {
		List<Region> regions = regionService.getAllRegions();
		List<Client> clients = clientService.getAllClients();
		model.addAttribute("regions", regions);
		model.addAttribute("clients", clients);
		model.addAttribute("demande", demande);

		Long selectedClientId = demande.getClient() != null ? demande.getClient().getId() : null;
		model.addAttribute("selectedClientId", selectedClientId);

		List<District> districts = List.of();
		List<Commune> communes = List.of();
		Long selectedRegionId = null;
		Long selectedDistrictId = null;
		Long selectedCommuneId = null;

		if (demande.getCommune() != null) {
			Commune commune = demande.getCommune();
			selectedCommuneId = commune.getId();
			District district = commune.getDistrict();
			if (district != null) {
				selectedDistrictId = district.getId();
				Region region = district.getRegion();
				if (region != null) {
					selectedRegionId = region.getId();
					districts = districtService.getDistrictsByRegion(region);
				}
				communes = communeService.getCommunesByDistrict(district);
			}
		}

		model.addAttribute("districts", districts);
		model.addAttribute("communes", communes);
		model.addAttribute("selectedRegionId", selectedRegionId);
		model.addAttribute("selectedDistrictId", selectedDistrictId);
		model.addAttribute("selectedCommuneId", selectedCommuneId);

		model.addAttribute("defaultDate", LocalDate.now().toString());
		model.addAttribute("defaultTime", LocalTime.now().withSecond(0).withNano(0)
			.format(DateTimeFormatter.ofPattern("HH:mm")));
	}

	private void populateStatutModel(Model model) {
		model.addAttribute("demandes", demandeService.getAllDemandes());
		model.addAttribute("statuses", statusService.getAllStatuses());
		model.addAttribute("defaultDate", LocalDate.now().toString());
		model.addAttribute("defaultTime", LocalTime.now().withSecond(0).withNano(0)
			.format(DateTimeFormatter.ofPattern("HH:mm")));
	}

	private Map<String, String> buildDemandeInfo(Demande demande) {
		Map<String, String> payload = new HashMap<>();
		payload.put("reference", safe(demande.getReference()));
		payload.put("clientNom", safe(demande.getClient().getNom()));
		payload.put("clientContact", safe(demande.getClient().getContact()));
		payload.put("clientAdresse", safe(demande.getClient().getAdresse()));
		payload.put("region", safe(demande.getCommune().getDistrict().getRegion().getLibelle()));
		payload.put("district", safe(demande.getCommune().getDistrict().getLibelle()));
		payload.put("commune", safe(demande.getCommune().getLibelle()));
		payload.put("lieu", safe(demande.getLieu()));
		DemandeStatut latest = getLatestStatut(demande);
		if (latest != null) {
			payload.put("statutId", String.valueOf(latest.getId()));
			payload.put("statutLibelle", safe(latest.getStatut() != null ? latest.getStatut().getLibelle() : ""));
			payload.put("statutStatusId", latest.getStatut() != null && latest.getStatut().getId() != null
				? String.valueOf(latest.getStatut().getId())
				: "");
			payload.put("statutDescription", safe(latest.getDescription()));
			payload.put("statutDate", formatDate(latest.getDate()));
			payload.put("statutTime", formatTime(latest.getDate()));
			payload.put("statutDuree", latest.getDureeTravail() == null ? "" : String.valueOf(latest.getDureeTravail()));
		} else {
			payload.put("statutId", "");
			payload.put("statutLibelle", "");
			payload.put("statutStatusId", "");
			payload.put("statutDescription", "");
			payload.put("statutDate", "");
			payload.put("statutTime", "");
			payload.put("statutDuree", "");
		}
		return payload;
	}

	private String safe(String value) {
		return value == null ? "" : value;
	}

	private String formatDate(Date date) {
		if (date == null) {
			return "";
		}
		LocalDate localDate = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()).toLocalDate();
		return localDate.toString();
	}

	private String formatTime(Date date) {
		if (date == null) {
			return "";
		}
		LocalTime localTime = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()).toLocalTime();
		return localTime.withSecond(0).withNano(0).format(DateTimeFormatter.ofPattern("HH:mm"));
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

	private DemandeStatut getLatestStatut(Demande demande) {
		if (demande == null || demande.getDemandeStatuts() == null) {
			return null;
		}
		return demande.getDemandeStatuts()
			.stream()
			.max(Comparator.comparing(DemandeStatut::getDate)
				.thenComparing(DemandeStatut::getId))
			.orElse(null);
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
}
