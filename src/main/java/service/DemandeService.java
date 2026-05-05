package service;

import model.Demande;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repository.DemandeRepository;
import java.util.List;

@Service
public class DemandeService {
    @Autowired
    private DemandeRepository demandeRepository;

    public void createDemande(Demande demande) {
        demandeRepository.save(demande);
    }

    public List<Demande> getAllDemandes() {
        return demandeRepository.findAll();
    }

    public Demande getDemandeById(int id) {
        return demandeRepository.findById(id).orElse(null);
    }

    public void updateDemande(Demande demande) {
        demandeRepository.save(demande);
    }

    public void deleteDemande(int id) {
        demandeRepository.deleteById(id);
    }

}
