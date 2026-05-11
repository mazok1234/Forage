package service;

import model.Devis;
import repository.DevisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class DevisService {

	@Autowired
	private DevisRepository devisRepository;

	public Devis createDevis(Devis devis) { return devisRepository.save(devis); }
	public List<Devis> getAllDevis() { return devisRepository.findAllWithDetails(); }
	public Optional<Devis> getDevisById(Long id) { return devisRepository.findById(id); }
	public Optional<Devis> getDevisByIdWithDetails(Long id) { return devisRepository.findByIdWithDetails(id); }
	public Devis updateDevis(Devis devis) { return devisRepository.save(devis); }
	public void deleteDevis(Long id) { devisRepository.deleteById(id); }
}
