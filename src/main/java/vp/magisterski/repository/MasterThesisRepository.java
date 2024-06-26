package vp.magisterski.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import vp.magisterski.model.magister.MasterThesis;
import vp.magisterski.model.enumerations.MasterThesisStatus;
import vp.magisterski.model.shared.Professor;

import java.util.List;

public interface MasterThesisRepository extends JpaRepository<MasterThesis, Long> {
    List<MasterThesis> findAll(Specification<MasterThesis> specification);
    Page<MasterThesis> findAll(Specification<MasterThesis> specification, Pageable pageable);
    List<MasterThesis> findByStudentIndex(String studentIndex);
    List<MasterThesis> findByMentor(Professor professor);
    List<MasterThesis> findMasterThesisByFirstMember(Professor professor);
    List<MasterThesis> findMasterThesisBySecondMember(Professor professor);
    Page<MasterThesis> findByStatus(MasterThesisStatus status, Pageable pageable);
    Page<MasterThesis> findByStatusIn(List<MasterThesisStatus> status, Pageable pageable);
}
