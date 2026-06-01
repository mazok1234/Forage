package service;

import model.TypeDevis;
import repository.TypeDevisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class TypeDevisService {
	@Autowired
	private TypeDevisRepository typeDevisRepository;

	public TypeDevis createTypeDevis(TypeDevis typeDevis) { return typeDevisRepository.save(typeDevis); }
	public List<TypeDevis> getAllTypeDevis() { return typeDevisRepository.findAll(); }
	public Optional<TypeDevis> getTypeDevisById(Long id) { return typeDevisRepository.findById(id); }
	public TypeDevis updateTypeDevis(TypeDevis typeDevis) { return typeDevisRepository.save(typeDevis); }
	public void deleteTypeDevis(Long id) { typeDevisRepository.deleteById(id); }
}
