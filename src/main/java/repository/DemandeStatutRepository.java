package repository;

import model.Demande;
import model.DemandeStatut;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface DemandeStatutRepository extends JpaRepository<DemandeStatut, Long> {
	Optional<DemandeStatut> findTopByDemandeOrderByDateDescIdDesc(Demande demande);
	Optional<DemandeStatut> findTopByDemandeAndIdNotOrderByDateDescIdDesc(Demande demande, Long id);
	List<DemandeStatut> findByDemandeOrderByDateAscIdAsc(Demande demande);

}
