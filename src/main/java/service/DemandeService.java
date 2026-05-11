package service;

import model.Demande;
import repository.DemandeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class DemandeService {

	@Autowired
	private DemandeRepository demandeRepository;

	public Demande createDemande(Demande demande) { return demandeRepository.save(demande); }
	public List<Demande> getAllDemandes() { return demandeRepository.findAllWithDetails(); }
	public Optional<Demande> getDemandeById(Long id) { return demandeRepository.findById(id); }
	public Optional<Demande> getDemandeByIdWithDetails(Long id) { return demandeRepository.findByIdWithDetails(id); }
	public Demande updateDemande(Demande demande) { return demandeRepository.save(demande); }
	public void deleteDemande(Long id) { demandeRepository.deleteById(id); }
}
