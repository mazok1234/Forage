package service;

import model.DemandeStatut;
import repository.DemandeStatutRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class DemandeStatutService {
	@Autowired
	private DemandeStatutRepository demandeStatutRepository;

	public DemandeStatut createDemandeStatut(DemandeStatut demandeStatut) { return demandeStatutRepository.save(demandeStatut); }
	public List<DemandeStatut> getAllDemandeStatuts() { return demandeStatutRepository.findAll(); }
	public Optional<DemandeStatut> getDemandeStatutById(Long id) { return demandeStatutRepository.findById(id); }
	public DemandeStatut updateDemandeStatut(DemandeStatut demandeStatut) { return demandeStatutRepository.save(demandeStatut); }
	public void deleteDemandeStatut(Long id) { demandeStatutRepository.deleteById(id); }
}
