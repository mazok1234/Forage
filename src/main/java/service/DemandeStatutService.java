package service;

import model.DemandeStatut;
import model.Demande;
import model.Status;
import repository.DemandeStatutRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;

@Service
public class DemandeStatutService {
	private static final LocalTime WORK_START = LocalTime.of(8, 0);
	private static final LocalTime WORK_END = LocalTime.of(16, 0);

	@Autowired
	private DemandeStatutRepository demandeStatutRepository;

	public DemandeStatut createDemandeStatut(DemandeStatut demandeStatut) { return demandeStatutRepository.save(demandeStatut); }
	public DemandeStatut createDemandeStatutWithDuration(Status status, Demande demande, String description, Date date) {
		Date effectiveDate = date == null ? new Date() : date;
		int durationMinutes = 0;
		DemandeStatut latest = demandeStatutRepository
			.findTopByDemandeOrderByDateDescIdDesc(demande)
			.orElse(null);
		if (latest != null && latest.getDate() != null) {
			Date clampedCurrent = clampToWorkWindow(effectiveDate);
			Date clampedPrevious = clampToWorkWindow(latest.getDate());
			long diffMs = clampedCurrent.getTime() - clampedPrevious.getTime();
			long minutes = diffMs / 60000;
			if (minutes < 0) {
				minutes = 0;
			}
			durationMinutes = minutes > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) minutes;
		}
		DemandeStatut demandeStatut = new DemandeStatut(status, demande, description, effectiveDate);
		demandeStatut.setDureeTravail(durationMinutes);
		return demandeStatutRepository.save(demandeStatut);
	}

	public DemandeStatut updateDemandeStatutWithDuration(DemandeStatut demandeStatut, Status status, String description, Date date) {
		Date effectiveDate = date == null ? new Date() : date;
		int durationMinutes = 0;
		Demande demande = demandeStatut.getDemande();
		if (demande != null && demandeStatut.getId() != null) {
			DemandeStatut previous = demandeStatutRepository
				.findTopByDemandeAndIdNotOrderByDateDescIdDesc(demande, demandeStatut.getId())
				.orElse(null);
			if (previous != null && previous.getDate() != null) {
				Date clampedCurrent = clampToWorkWindow(effectiveDate);
				Date clampedPrevious = clampToWorkWindow(previous.getDate());
				long diffMs = clampedCurrent.getTime() - clampedPrevious.getTime();
				long minutes = diffMs / 60000;
				if (minutes < 0) {
					minutes = 0;
				}
				durationMinutes = minutes > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) minutes;
			}
		}
		demandeStatut.setStatut(status);
		demandeStatut.setDescription(description);
		demandeStatut.setDate(effectiveDate);
		demandeStatut.setDureeTravail(durationMinutes);
		return demandeStatutRepository.save(demandeStatut);
	}

	private Date clampToWorkWindow(Date input) {
		LocalDateTime dateTime = LocalDateTime.ofInstant(input.toInstant(), ZoneId.systemDefault());
		LocalTime time = dateTime.toLocalTime();
		if (time.isBefore(WORK_START)) {
			time = WORK_START;
		} else if (time.isAfter(WORK_END)) {
			time = WORK_END;
		}
		LocalDateTime clamped = LocalDateTime.of(dateTime.toLocalDate(), time);
		return Date.from(clamped.atZone(ZoneId.systemDefault()).toInstant());
	}
	public List<DemandeStatut> getAllDemandeStatuts() { return demandeStatutRepository.findAll(); }
	public Optional<DemandeStatut> getDemandeStatutById(Long id) { return demandeStatutRepository.findById(id); }
	public DemandeStatut updateDemandeStatut(DemandeStatut demandeStatut) { return demandeStatutRepository.save(demandeStatut); }
	public void deleteDemandeStatut(Long id) { demandeStatutRepository.deleteById(id); }
}
