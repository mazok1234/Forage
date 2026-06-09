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
import java.text.Normalizer;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
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
			durationMinutes = computeWorkingMinutes(latest.getDate(), effectiveDate);
		}
		DemandeStatut demandeStatut = new DemandeStatut(status, demande, description, effectiveDate);
		demandeStatut.setDureeTravail(durationMinutes);
		demandeStatut.setDureeTotal(isFinishedStatus(status) ? sumExistingDuration(demande, null) + durationMinutes : 0);
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
				durationMinutes = computeWorkingMinutes(previous.getDate(), effectiveDate);
			}
		}
		demandeStatut.setStatut(status);
		demandeStatut.setDescription(description);
		demandeStatut.setDate(effectiveDate);
		demandeStatut.setDureeTravail(durationMinutes);
		demandeStatut.setDureeTotal(isFinishedStatus(status) ? sumExistingDuration(demande, demandeStatut.getId()) + durationMinutes : 0);
		return demandeStatutRepository.save(demandeStatut);
	}

	private boolean isFinishedStatus(Status status) {
		if (status == null || status.getLibelle() == null) {
			return false;
		}
		String libelle = Normalizer.normalize(status.getLibelle().toLowerCase(), Normalizer.Form.NFD)
			.replaceAll("\\p{M}", "");
		return libelle.contains("termine");
	}

	private int sumExistingDuration(Demande demande, Long excludedId) {
		if (demande == null) {
			return 0;
		}
		long total = 0;
		for (DemandeStatut statut : demandeStatutRepository.findByDemandeOrderByDateAscIdAsc(demande)) {
			if (excludedId != null && excludedId.equals(statut.getId())) {
				continue;
			}
			Integer duration = statut.getDureeTravail();
			if (duration != null && duration > 0) {
				total += duration;
			}
		}
		return total > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) total;
	}

	private int computeWorkingMinutes(Date startDate, Date endDate) {
		if (startDate == null || endDate == null) {
			return 0;
		}
		LocalDateTime start = LocalDateTime.ofInstant(startDate.toInstant(), ZoneId.systemDefault());
		LocalDateTime end = LocalDateTime.ofInstant(endDate.toInstant(), ZoneId.systemDefault());
		if (end.isBefore(start)) {
			return 0;
		}
		LocalDate current = start.toLocalDate();
		LocalDate endDay = end.toLocalDate();
		long minutes = 0;
		while (!current.isAfter(endDay)) {
			DayOfWeek dayOfWeek = current.getDayOfWeek();
			if (dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY) {
				LocalTime dayStart = WORK_START;
				LocalTime dayEnd = WORK_END;
				if (current.equals(start.toLocalDate())) {
					LocalTime candidate = start.toLocalTime();
					if (candidate.isAfter(dayStart)) {
						dayStart = candidate;
					}
				}
				if (current.equals(end.toLocalDate())) {
					LocalTime candidate = end.toLocalTime();
					if (candidate.isBefore(dayEnd)) {
						dayEnd = candidate;
					}
				}
				if (dayStart.isBefore(WORK_START)) {
					dayStart = WORK_START;
				}
				if (dayEnd.isAfter(WORK_END)) {
					dayEnd = WORK_END;
				}
				if (dayEnd.isAfter(dayStart)) {
					minutes += Duration.between(dayStart, dayEnd).toMinutes();
				}
			}
			current = current.plusDays(1);
		}
		return minutes > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) minutes;
	}
	public List<DemandeStatut> getAllDemandeStatuts() { return demandeStatutRepository.findAll(); }
	public Optional<DemandeStatut> getDemandeStatutById(Long id) { return demandeStatutRepository.findById(id); }
	public DemandeStatut updateDemandeStatut(DemandeStatut demandeStatut) { return demandeStatutRepository.save(demandeStatut); }
	public void deleteDemandeStatut(Long id) { demandeStatutRepository.deleteById(id); }
}
