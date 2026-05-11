package service;

import model.DevisStatut;
import repository.DevisStatutRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class DevisStatutService {
	@Autowired
	private DevisStatutRepository devisStatutRepository;

	public DevisStatut createDevisStatut(DevisStatut devisStatut) { return devisStatutRepository.save(devisStatut); }
	public List<DevisStatut> getAllDevisStatuts() { return devisStatutRepository.findAll(); }
	public Optional<DevisStatut> getDevisStatutById(Long id) { return devisStatutRepository.findById(id); }
	public DevisStatut updateDevisStatut(DevisStatut devisStatut) { return devisStatutRepository.save(devisStatut); }
	public void deleteDevisStatut(Long id) { devisStatutRepository.deleteById(id); }
}
