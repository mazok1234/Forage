package controller;

import model.Demande;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import service.DemandeService;

@Controller
public class DemandeController {

    @Autowired
    private DemandeService demandeService;

    @GetMapping("/demande/new")
    public String createForm(Model model) {
        model.addAttribute("demande", new Demande());
        return "createDemande";
    }

    @PostMapping("/demande/save")
    public String saveDemande(@ModelAttribute Demande demande, Model model) {
        demandeService.createDemande(demande);
        model.addAttribute("message", "Demande créée avec succès");
        return "success";
    }
}
