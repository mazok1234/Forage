package repository;

import model.Devis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface DevisRepository extends JpaRepository<Devis, Long> {
	@Query("""
		select distinct dv from Devis dv
		join fetch dv.demande d
		join fetch d.client
		join fetch d.commune c
		join fetch c.district dist
		join fetch dist.region
		left join fetch dv.devisStatuts ds
		left join fetch ds.statut
	""")
	List<Devis> findAllWithDetails();

	@Query("""
		select distinct dv from Devis dv
		join fetch dv.demande d
		join fetch d.client
		join fetch d.commune c
		join fetch c.district dist
		join fetch dist.region
		left join fetch dv.devisStatuts ds
		left join fetch ds.statut
		where dv.id = :id
	""")
	Optional<Devis> findByIdWithDetails(@Param("id") Long id);
}
