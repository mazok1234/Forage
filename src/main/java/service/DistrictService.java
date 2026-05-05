package service;

import model.District;
import model.Region;
import repository.DistrictRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class DistrictService {
	@Autowired
	private DistrictRepository districtRepository;

	public District createDistrict(District district) { return districtRepository.save(district); }
	public List<District> getAllDistricts() { return districtRepository.findAll(); }
	public Optional<District> getDistrictById(Long id) { return districtRepository.findById(id); }
	public District updateDistrict(District district) { return districtRepository.save(district); }
	public void deleteDistrict(Long id) { districtRepository.deleteById(id); }
	public List<District> getDistrictsByRegion(Region region) { return districtRepository.findByRegion(region); }
}
