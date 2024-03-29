package vp.magisterski.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.multipart.MultipartFile;
import vp.magisterski.model.magister.MasterThesis;
import vp.magisterski.model.enumerations.MasterThesisStatus;
import vp.magisterski.model.shared.Professor;
import vp.magisterski.model.shared.Student;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MasterThesisService {
    MasterThesis save(String studentIndex, LocalDateTime dateTime, String title, String area,
                      String description, String mentorId, String firstMemberId,
                      String secondMemberId);

    MasterThesis newThesis(String studentIndex, String title, String mentorId, String description);

    Optional<MasterThesis> findThesisById(Long id);

    List<MasterThesis> findByStudentIndex(String id);
    List<MasterThesis> findByMentorIndex(String id);
    List<MasterThesis> findByMemberIndex(String name);

    Page<MasterThesis> findAll(Pageable pageable);

    Specification<MasterThesis> filterMasterThesis(Student student, String title, MasterThesisStatus status,
                                                   Professor mentor, Professor member, String isValidation);

    Specification<MasterThesis> filterMasterThesis(MasterThesis masterThesis, String isValidation);

    Page<MasterThesis> findAll(Specification<MasterThesis> specification, Pageable pageable);

    List<MasterThesis> findAll();

    Page<MasterThesis> findAllByStatus(MasterThesisStatus status, Pageable pageable);

    void cancelMasterThesis(Long id);

    void saveFile(Long id, MultipartFile file) throws IOException;

    void updateStatus(Long thesisId, MasterThesisStatus status);

    void setCommission(Long thesisId, String firstMember, String secondMember);
    void updateLocationAndDate(Long thesisId, String room, LocalDateTime time);
    void updateOblast(Long thesisId, String area);
    Page<MasterThesis> findAllByStatusOrderGreaterThan(List<MasterThesisStatus> statuses, Pageable pageable);

    void updateArchiveNumber(Long thesisId, String archiveNumber);
}
