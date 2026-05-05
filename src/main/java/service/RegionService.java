package service;

import model.Region;
import repository.RegionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class RegionService {
	@Autowired
	private RegionRepository regionRepository;

	public Region createRegion(Region region) { return regionRepository.save(region); }
	public List<Region> getAllRegions() { return regionRepository.findAll(); }
	public Optional<Region> getRegionById(Long id) { return regionRepository.findById(id); }
	public Region updateRegion(Region region) { return regionRepository.save(region); }
	public void deleteRegion(Long id) { regionRepository.deleteById(id); }
}
