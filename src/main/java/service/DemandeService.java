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
	public Optional<Demande> getDemandeByReferenceWithDetails(String reference) { return demandeRepository.findByReferenceWithDetails(reference); }
	public Demande updateDemande(Demande demande) { return demandeRepository.save(demande); }
	public void deleteDemande(Long id) { demandeRepository.deleteById(id); }

	public String getNextReference() {
		Long maxId = demandeRepository.findMaxId();
		long next = (maxId == null ? 1L : maxId + 1L);
		return "DMD" + String.format("%03d", next);
	}
}
