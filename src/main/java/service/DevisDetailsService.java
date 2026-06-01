package service;

import model.DevisDetails;
import repository.DevisDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class DevisDetailsService {
	@Autowired
	private DevisDetailsRepository devisDetailsRepository;

	public DevisDetails createDevisDetails(DevisDetails devisDetails) { return devisDetailsRepository.save(devisDetails); }
	public List<DevisDetails> getAllDevisDetails() { return devisDetailsRepository.findAll(); }
	public Optional<DevisDetails> getDevisDetailsById(Long id) { return devisDetailsRepository.findById(id); }
	public DevisDetails updateDevisDetails(DevisDetails devisDetails) { return devisDetailsRepository.save(devisDetails); }
	public void deleteDevisDetails(Long id) { devisDetailsRepository.deleteById(id); }
}
