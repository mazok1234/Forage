package controller;

import model.*;
import service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequestMapping("/demande")
public class DemandeController {

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

	@GetMapping("/")
	public String showForm(Model model) {
		List<Region> regions = regionService.getAllRegions();
		List<Client> clients = clientService.getAllClients();
		model.addAttribute("regions", regions);
		model.addAttribute("clients", clients);
		model.addAttribute("demande", new Demande());
		return "demande/form";
	}

	@GetMapping("/districts/{regionId}")
	@ResponseBody
	public ResponseEntity<List<District>> getDistrictsByRegion(@PathVariable Long regionId) {
		return regionService.getRegionById(regionId)
			.map(region -> ResponseEntity.ok(districtService.getDistrictsByRegion(region)))
			.orElse(ResponseEntity.notFound().build());
	}

	@GetMapping("/communes/{districtId}")
	@ResponseBody
	public ResponseEntity<List<Commune>> getCommunesByDistrict(@PathVariable Long districtId) {
		return districtService.getDistrictById(districtId)
			.map(district -> ResponseEntity.ok(communeService.getCommunesByDistrict(district)))
			.orElse(ResponseEntity.notFound().build());
	}

	@PostMapping("/create")
	public String createDemande(
			@RequestParam Long clientId, 
			@RequestParam Long communeId, 
			@RequestParam String lieu,
			Model model) {
		try {
			Client client = clientService.getClientById(clientId).orElse(null);
			Commune commune = communeService.getCommuneById(communeId).orElse(null);
			
			if (client != null && commune != null) {
				Demande demande = new Demande(client, commune);
				demande.setLieu(lieu);
				
				Demande savedDemande = demandeService.createDemande(demande);
				
				model.addAttribute("message", "Demande créée avec succès");
				model.addAttribute("demande", savedDemande);
				return "demande/success";
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
}
