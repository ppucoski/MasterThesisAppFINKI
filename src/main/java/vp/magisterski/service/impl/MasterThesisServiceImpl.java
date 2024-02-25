package vp.magisterski.service.impl;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vp.magisterski.model.exceptions.ProfessorDoesNotExistException;
import vp.magisterski.model.exceptions.StudentDoesNotExistException;
import vp.magisterski.model.magister.MasterThesis;
import vp.magisterski.model.magister.MasterThesisPresentation;
import vp.magisterski.model.magister.MasterThesisStatus;
import vp.magisterski.model.shared.Professor;
import vp.magisterski.model.shared.Student;
import vp.magisterski.repository.MasterThesisRepository;
import vp.magisterski.repository.ProfessorRepository;
import vp.magisterski.repository.StudentRepository;
import vp.magisterski.service.MasterThesisService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Service
public class MasterThesisServiceImpl implements MasterThesisService {

    private final MasterThesisRepository masterThesisRepository;
    private final StudentRepository studentRepository;
    private final ProfessorRepository professorRepository;

    public MasterThesisServiceImpl(MasterThesisRepository masterThesisRepository, StudentRepository studentRepository, ProfessorRepository professorRepository) {
        this.masterThesisRepository = masterThesisRepository;
        this.studentRepository = studentRepository;
        this.professorRepository = professorRepository;
    }

    @Override
    public MasterThesis save(String studentIndex, LocalDateTime dateTime, String title, String area, String description, String mentorId, String firstMemberId, String secondMemberId) {
        Student student = this.studentRepository.findById(studentIndex)
                .orElseThrow(() -> new StudentDoesNotExistException(studentIndex));
        Professor mentor = this.professorRepository.findById(mentorId)
                .orElseThrow(() -> new ProfessorDoesNotExistException(mentorId));
        Professor firstMember = this.professorRepository.findById(firstMemberId)
                .orElseThrow(() -> new ProfessorDoesNotExistException(firstMemberId));
        Professor secondMember = this.professorRepository.findById(secondMemberId)
                .orElseThrow(() -> new ProfessorDoesNotExistException(secondMemberId));
        MasterThesisPresentation presentation = new MasterThesisPresentation("", dateTime);
        MasterThesis masterThesis = new MasterThesis(MasterThesisStatus.STUDENT_THESIS_REGISTRATION, presentation,
                student, title, area, description, mentor, firstMember, secondMember);
        return this.masterThesisRepository.save(masterThesis);
    }

    @Override
    public Optional<MasterThesis> findThesisById(Long id) {
        return this.masterThesisRepository.findById(id);
    }

    @Override
    public Page<MasterThesis> findAll(Pageable pageable) {
        return this.masterThesisRepository.findAll(pageable);
    }

    @Override
    public Specification<MasterThesis> filterMasterThesis(Student student, String title, MasterThesisStatus status,
                                                          Professor mentor, Professor member, String isValidation) {
        return (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();

            if (student != null) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.equal(root.get("student"), student));
            }

            if (status != null) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.equal(root.get("status"), status));
            }

            if (mentor != null) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.equal(root.get("mentor"), mentor));
            }

            if (member != null) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.or(
                                criteriaBuilder.equal(root.get("firstMember"), member),
                                criteriaBuilder.equal(root.get("secondMember"), member)
                        ));
            }

            if (title != null && !title.isEmpty()) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.like(root.get("title"), "%" + title + "%"));
            }

            if ("VALIDATION".equals(isValidation)) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("status")), "%validation%"));
            }

            return predicate;
        };
    }

    @Override
    public Specification<MasterThesis> filterMasterThesis(MasterThesis masterThesis, String isValidation) {
        return (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();

            if (masterThesis.getStudent() != null) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.equal(root.get("student"), masterThesis.getStudent()));
            }

            if (masterThesis.getStatus() != null) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.equal(root.get("status"), masterThesis.getStatus()));
            }

            if (masterThesis.getMentor() != null) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.equal(root.get("mentor"), masterThesis.getMentor()));
            }

            if (masterThesis.getFirstMember() != null || masterThesis.getSecondMember() != null) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.or(
                                criteriaBuilder.equal(root.get("firstMember"), masterThesis.getFirstMember()),
                                criteriaBuilder.equal(root.get("secondMember"), masterThesis.getSecondMember())
                        ));
            }

            if (masterThesis.getTitle() != null && !masterThesis.getTitle().isEmpty()) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.like(root.get("title"), "%" + masterThesis.getTitle() + "%"));
            }

            if ("VALIDATION".equals(isValidation)) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("status")), "%validation%"));
            }

            return predicate;
        };
    }


    @Override
    public Page<MasterThesis> findAll(Specification<MasterThesis> specification, Pageable pageable) {
        return this.masterThesisRepository.findAll(specification, pageable);
    }


    @Override
    public List<MasterThesis> findAll() {
        return this.masterThesisRepository.findAll();
    }

    @Override
    public void cancelMasterThesis(Long id) {
        MasterThesis masterThesis = this.findThesisById(id).get();
        masterThesis.setStatus(MasterThesisStatus.CANCELLED);
        this.masterThesisRepository.save(masterThesis);
    }

    @Override
    public void saveFile(Long id, MultipartFile file) throws IOException {
        MasterThesis masterThesis = this.findThesisById(id).get();
        masterThesis.setThesisText(file.getBytes());
        this.masterThesisRepository.save(masterThesis);
    }

}
