package vp.magisterski.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vp.magisterski.model.accreditations.StudyProgramDetails;
import vp.magisterski.model.shared.Student;

public interface StudyProgramDetailsRepository extends JpaRepository<StudyProgramDetails, String> {
}
