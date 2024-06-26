package vp.magisterski.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.multipart.MultipartFile;
import vp.magisterski.model.enumerations.MasterThesisDocumentType;
import vp.magisterski.model.magister.MasterThesis;
import vp.magisterski.model.enumerations.MasterThesisStatus;
import vp.magisterski.model.shared.Professor;
import vp.magisterski.model.shared.Student;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MasterThesisService {
    /*MasterThesis save(String studentIndex, LocalDateTime dateTime, String title, String area,
                      String description, String mentorId, String firstMemberId,
                      String secondMemberId);*/

    MasterThesis newThesis(String studentIndex, String title, String mentorId, String description);

    Optional<MasterThesis> findThesisById(Long id);

    List<MasterThesis> findByStudentIndex(String id);
    List<MasterThesis> findByMentorIndex(String id);
    List<MasterThesis> findByMemberIndex(String name);

    Page<MasterThesis> findAll(Pageable pageable);

    Specification<MasterThesis> filterMasterThesis(String studentIndex, String title, MasterThesisStatus status,
                                                   Professor mentor, Professor member1, Professor member2, String isValidation);

    Specification<MasterThesis> filterMasterThesis(MasterThesis masterThesis, String isValidation);
    Specification<MasterThesis> filterMasterThesisByMentor(Professor mentor);
    Specification<MasterThesis> filterMasterThesisByStudent(Student student);
    Specification<MasterThesis> filterMasterThesisByMember(Professor firstMember, Professor secondMember);
    Specification<MasterThesis> filterMasterThesisByFirstMember(Professor firstMember);
    Specification<MasterThesis> filterMasterThesisBySecondMember(Professor secondMember);

    Page<MasterThesis> findAll(Specification<MasterThesis> specification, Pageable pageable);

    List<MasterThesis> findAll();

    Page<MasterThesis> findAllByStatus(MasterThesisStatus status, Pageable pageable);

    void cancelMasterThesis(Long id);

    void saveFile(Long id, MasterThesisDocumentType type, MultipartFile file) throws IOException;

    void updateStatus(Long thesisId, MasterThesisStatus status);

    void setCommission(Long thesisId, String firstMember, String secondMember);
    void updateLocationAndDate(Long thesisId, String room, LocalDateTime time);
    void updateOblast(Long thesisId, String area);

    void updateArchiveNumber(Long thesisId, String archiveNumber);
    Specification<MasterThesis> filterMasterThesisByStatus(MasterThesisStatus status);
    List<MasterThesisStatus> returnStatus();

    void updateMasterThesis(Long id, MasterThesis thesis);

    void setLastUpdateMasterThesis(Long thesisId);
    void addGrade(Long id, Integer grade);
}
