package service;

import model.Commune;
import model.District;
import repository.CommuneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CommuneService {
	@Autowired
	private CommuneRepository communeRepository;

	public Commune createCommune(Commune commune) { return communeRepository.save(commune); }
	public List<Commune> getAllCommunes() { return communeRepository.findAll(); }
	public Optional<Commune> getCommuneById(Long id) { return communeRepository.findById(id); }
	public Commune updateCommune(Commune commune) { return communeRepository.save(commune); }
	public void deleteCommune(Long id) { communeRepository.deleteById(id); }
	public List<Commune> getCommunesByDistrict(District district) { return communeRepository.findByDistrict(district); }
}
