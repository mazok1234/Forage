package repository;

import model.District;
import model.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DistrictRepository extends JpaRepository<District, Long> {
	List<District> findByRegion(Region region);
}
