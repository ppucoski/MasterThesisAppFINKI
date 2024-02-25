package vp.magisterski.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.multipart.MultipartFile;
import vp.magisterski.model.magister.MasterThesis;
import vp.magisterski.model.magister.MasterThesisStatus;
import vp.magisterski.model.shared.Professor;
import vp.magisterski.model.shared.Student;
import vp.magisterski.repository.MasterThesisRepository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MasterThesisService {
    MasterThesis save(String studentIndex, LocalDateTime dateTime, String title, String area,
                      String description, String mentorId, String firstMemberId,
                      String secondMemberId);

    Optional<MasterThesis> findThesisById(Long id);

    Page<MasterThesis> findAll(Pageable pageable);

    Specification<MasterThesis> filterMasterThesis(Student student, String title, MasterThesisStatus status,
                                                   Professor mentor, Professor member, String isValidation);
    Specification<MasterThesis> filterMasterThesis(MasterThesis masterThesis, String isValidation);
    //Page<MasterThesis> findAll(List<MasterThesis> masterThesisList, Pageable pageable);
    Page<MasterThesis> findAll(Specification<MasterThesis> specification, Pageable pageable);
    List<MasterThesis> findAll();

    void cancelMasterThesis(Long id);

    void saveFile(Long id, MultipartFile file) throws IOException;
}
