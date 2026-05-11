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

@Controller
@RequestMapping("/demande")
public class DemandeController {

	private record OptionDto(Long id, String libelle) {}
	private static final Long STATUT_DEMANDE_EN_ATTENTE_ID = 1L;

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
			Model model) {
		try {
			Client client = clientService.getClientById(clientId).orElse(null);
			Commune commune = communeService.getCommuneById(communeId).orElse(null);
			
			if (client != null && commune != null) {
				Demande demande = new Demande(client, commune);
				demande.setLieu(lieu);
				
				Demande savedDemande = demandeService.createDemande(demande);
				statusService.getStatusById(STATUT_DEMANDE_EN_ATTENTE_ID).ifPresent(status -> {
					DemandeStatut demandeStatut = new DemandeStatut(
						status,
						savedDemande,
						"Creation de la demande",
						new Date()
					);
					demandeStatutService.createDemandeStatut(demandeStatut);
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
			@RequestParam(value = "description", required = false) String description) {
		Demande demande = demandeService.getDemandeById(id).orElse(null);
		Status status = statusService.getStatusById(statusId).orElse(null);
		if (demande == null || status == null) {
			return "redirect:/demande/list";
		}

		DemandeStatut demandeStatut = new DemandeStatut(
			status,
			demande,
			description,
			new Date()
		);
		demandeStatutService.createDemandeStatut(demandeStatut);
		return "redirect:/demande/list";
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
