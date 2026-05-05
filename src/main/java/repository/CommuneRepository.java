package repository;

import model.Commune;
import model.District;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommuneRepository extends JpaRepository<Commune, Long> {
	List<Commune> findByDistrict(District district);
}
