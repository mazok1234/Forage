package repository;

import model.Demande;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface DemandeRepository extends JpaRepository<Demande, Integer> {
    
}
