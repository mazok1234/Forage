package repository;

import model.Demande;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface DemandeRepository extends JpaRepository<Demande, Long> {
	@Query("""
		select distinct d from Demande d
		join fetch d.client
		join fetch d.commune c
		join fetch c.district dist
		join fetch dist.region
		left join fetch d.demandeStatuts ds
		left join fetch ds.statut
	""")
	List<Demande> findAllWithDetails();

	@Query("select max(d.id) from Demande d")
	Long findMaxId();

	@Query("""
		select distinct d from Demande d
		join fetch d.client
		join fetch d.commune c
		join fetch c.district dist
		join fetch dist.region
		left join fetch d.demandeStatuts ds
		left join fetch ds.statut
		where d.id = :id
	""")
	Optional<Demande> findByIdWithDetails(@Param("id") Long id);

	@Query("""
		select distinct d from Demande d
		join fetch d.client
		join fetch d.commune c
		join fetch c.district dist
		join fetch dist.region
		left join fetch d.demandeStatuts ds
		left join fetch ds.statut
		where d.reference = :reference
	""")
	Optional<Demande> findByReferenceWithDetails(@Param("reference") String reference);

}
