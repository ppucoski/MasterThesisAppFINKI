package vp.magisterski.service.impl;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import vp.magisterski.model.enumerations.MasterThesisDocumentType;
import vp.magisterski.model.exceptions.ProfessorDoesNotExistException;
import vp.magisterski.model.exceptions.RoomDoesNotExistsException;
import vp.magisterski.model.exceptions.StudentDoesNotExistException;
import vp.magisterski.model.magister.MasterThesis;
import vp.magisterski.model.magister.MasterThesisPresentation;
import vp.magisterski.model.enumerations.MasterThesisStatus;
import vp.magisterski.model.shared.Professor;
import vp.magisterski.model.shared.Room;
import vp.magisterski.model.shared.Student;
import vp.magisterski.repository.*;
import vp.magisterski.service.MasterThesisService;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


@Service
public class MasterThesisServiceImpl implements MasterThesisService {

    private final MasterThesisRepository masterThesisRepository;
    private final StudentRepository studentRepository;
    private final ProfessorRepository professorRepository;
    private final RoomRepository roomRepository;
    private final MasterThesisStatusChangeRepository masterThesisStatusChangeRepository;
    private final MasterThesisDocumentServiceImpl masterThesisDocumentServiceImpl;

    public MasterThesisServiceImpl(MasterThesisRepository masterThesisRepository, StudentRepository studentRepository, ProfessorRepository professorRepository, RoomRepository roomRepository,
                                   MasterThesisStatusChangeRepository masterThesisStatusChangeRepository, MasterThesisDocumentServiceImpl masterThesisDocumentServiceImpl) {
        this.masterThesisRepository = masterThesisRepository;
        this.studentRepository = studentRepository;
        this.professorRepository = professorRepository;
        this.roomRepository = roomRepository;
        this.masterThesisStatusChangeRepository = masterThesisStatusChangeRepository;
        this.masterThesisDocumentServiceImpl = masterThesisDocumentServiceImpl;
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
    public MasterThesis newThesis(String studentIndex, String title, String mentorId, String description) {
        Student student = this.studentRepository.findById(studentIndex)
                .orElseThrow(() -> new StudentDoesNotExistException(studentIndex));
        Professor mentor = this.professorRepository.findById(mentorId)
                .orElseThrow(() -> new ProfessorDoesNotExistException(mentorId));
        MasterThesis masterThesis = new MasterThesis(MasterThesisStatus.STUDENT_THESIS_REGISTRATION, student, title, mentor, description);
        return this.masterThesisRepository.save(masterThesis);
    }

    @Override
    public Optional<MasterThesis> findThesisById(Long id) {
        return this.masterThesisRepository.findById(id);
    }

    @Override
    public List<MasterThesis> findByStudentIndex(String id) {
        return this.masterThesisRepository.findByStudentIndex(id);
    }

    @Override
    public List<MasterThesis> findByMentorIndex(String name) {
        Professor mentor = this.professorRepository.findProfessorByName(name);
        return this.masterThesisRepository.findByMentor(mentor);
    }

    @Override
    public List<MasterThesis> findByMemberIndex(String name) {
        Professor member = this.professorRepository.findProfessorByName(name);
        List<MasterThesis> firstMember = this.masterThesisRepository.findMasterThesisByFirstMember(member);
        List<MasterThesis> secondMember = this.masterThesisRepository.findMasterThesisBySecondMember(member);
        List<MasterThesis> allThesis = new ArrayList<>();
        allThesis.addAll(firstMember);
        allThesis.addAll(secondMember);
        return allThesis;
    }


    @Override
    public Page<MasterThesis> findAll(Pageable pageable) {
        return this.masterThesisRepository.findAll(pageable);
    }

    @Override
    public Specification<MasterThesis> filterMasterThesis(String studentIndex, String title, MasterThesisStatus status,
                                                          Professor mentor, Professor member1, Professor member2, String isValidation) {
        return (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();

            if (studentIndex != null && !studentIndex.isEmpty()) {
                List<Student> students = studentRepository.findByIndexStartingWith(studentIndex);
                if(!students.isEmpty()){
                    predicate = criteriaBuilder.and(predicate,
                            root.get("student").in(students));
                }
                else{
                    predicate =  criteriaBuilder.disjunction();
                }
            }

            if (status != null) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.equal(root.get("status"), status));
            }

            if (mentor != null) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.equal(root.get("mentor"), mentor));
            }

            if (member1 != null && member2 != null) {
                predicate = criteriaBuilder.or(predicate,
                        criteriaBuilder.and(
                                criteriaBuilder.equal(root.get("firstMember"), member1),
                                criteriaBuilder.equal(root.get("secondMember"), member2)
                        ),criteriaBuilder.and(
                                criteriaBuilder.equal(root.get("firstMember"), member2),
                                criteriaBuilder.equal(root.get("secondMember"), member1)
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

            if (masterThesis.getFirstMember() != null && masterThesis.getSecondMember() != null) {
                predicate = criteriaBuilder.or(predicate,
                        criteriaBuilder.and(
                                criteriaBuilder.equal(root.get("firstMember"), masterThesis.getFirstMember()),
                                criteriaBuilder.equal(root.get("secondMember"), masterThesis.getSecondMember())
                        ),
                        criteriaBuilder.and(
                                criteriaBuilder.equal(root.get("firstMember"), masterThesis.getSecondMember()),
                                criteriaBuilder.equal(root.get("secondMember"), masterThesis.getFirstMember())
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
    public List<MasterThesisStatus> returnStatus(){
        return Arrays.asList(
                MasterThesisStatus.MENTOR_COMMISSION_CHOICE,
                MasterThesisStatus.SECOND_SECRETARY_VALIDATION,
                MasterThesisStatus.COMMISSION_CHECK,
                MasterThesisStatus.THIRD_SECRETARY_VALIDATION,
                MasterThesisStatus.DRAFT_CHECK,
                MasterThesisStatus.REPORT_VALIDATION,
                MasterThesisStatus.FOURTH_SECRETARY_VALIDATION,
                MasterThesisStatus.ADMINISTRATION_ARCHIVING,
                MasterThesisStatus.PROCESS_FINISHED
        );
    }

    @Override
    public void updateMasterThesis(Long id, MasterThesis thesis) {
        MasterThesis mt = masterThesisRepository.findById(id).orElse(null);
        if (mt != null) {
            mt.setArchiveNumber(thesis.getArchiveNumber());
            mt.setTitle(thesis.getTitle());
            mt.setArea(thesis.getArea());
            mt.setDescription(thesis.getDescription());
            mt.setMentor(thesis.getMentor());
            mt.setFirstMember(thesis.getFirstMember());
            mt.setSecondMember(thesis.getSecondMember());
            mt.setPresentation(thesis.getPresentation());
            masterThesisRepository.save(mt);
        }

    }


    @Override
    public Specification<MasterThesis> filterMasterThesisByStatus(MasterThesisStatus status) {
        return (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();

            if (status != null) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.equal(root.get("status"), status));
            } else {

                predicate = criteriaBuilder.and(predicate,
                        root.get("status").in(this.returnStatus()));
            }

            return predicate;
        };
    }

    @Override
    public Specification<MasterThesis> filterMasterThesisByMentor(Professor mentor) {
        return (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();

            if (mentor != null) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.equal(root.get("mentor"), mentor));
            }

            return predicate;
        };
    }

    @Override
    public Specification<MasterThesis> filterMasterThesisByStudent(Student student) {
        return (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();


            if (student != null) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.equal(root.get("student"), student));
            }


            return predicate;
        };
    }

    @Override
    public Specification<MasterThesis> filterMasterThesisByMember(Professor firstMember, Professor secondMember) {
        return (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();

            if (firstMember != null && secondMember != null) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.or(
                                criteriaBuilder.and(
                                        criteriaBuilder.equal(root.get("firstMember"), firstMember),
                                        criteriaBuilder.equal(root.get("secondMember"), secondMember)
                                ),
                                criteriaBuilder.and(
                                        criteriaBuilder.equal(root.get("firstMember"), secondMember),
                                        criteriaBuilder.equal(root.get("secondMember"), firstMember)
                                )
                        ));
            }

            return predicate;
        };
    }

    @Override
    public Specification<MasterThesis> filterMasterThesisByFirstMember(Professor firstMember) {
        return (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();


            predicate = criteriaBuilder.and(predicate,
                    criteriaBuilder.or(
                            criteriaBuilder.and(
                                    criteriaBuilder.equal(root.get("firstMember"), firstMember)
                            ),
                            criteriaBuilder.and(
                                    criteriaBuilder.equal(root.get("secondMember"), firstMember)
                            )
                    ));


            return predicate;
        };
    }

    @Override
    public Specification<MasterThesis> filterMasterThesisBySecondMember(Professor secondMember) {
        return (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();


            predicate = criteriaBuilder.and(predicate,
                    criteriaBuilder.or(
                            criteriaBuilder.and(
                                    criteriaBuilder.equal(root.get("firstMember"), secondMember)
                                    ),
                            criteriaBuilder.and(
                                    criteriaBuilder.equal(root.get("secondMember"), secondMember)
                                    )
                    ));


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
    public Page<MasterThesis> findAllByStatus(MasterThesisStatus status, Pageable pageable) {
        return this.masterThesisRepository.findByStatus(status, pageable);
    }

    @Override
    public void cancelMasterThesis(Long id) {
        MasterThesis masterThesis = this.findThesisById(id).get();
        masterThesis.setStatus(MasterThesisStatus.CANCELLED);
        this.masterThesisRepository.save(masterThesis);
    }

    @Override
    @Transactional
    public void saveFile(Long id, MasterThesisDocumentType type, MultipartFile file) throws IOException {
        Optional<MasterThesis> optionalMasterThesis = masterThesisRepository.findById(id);
        if (optionalMasterThesis.isPresent()) {
            MasterThesis masterThesis = optionalMasterThesis.get();
            try (InputStream inputStream = file.getInputStream()) {
                masterThesis.setThesisText(inputStream.readAllBytes());
            }
            masterThesisRepository.save(masterThesis);
        } else {
            throw new IllegalArgumentException("Master thesis not found with ID: " + id);
        }
    }

    @Override
    public void updateStatus(Long thesisId, MasterThesisStatus status) {
        MasterThesis thesis = masterThesisRepository.findById(thesisId).orElse(null);
        if (thesis != null) {
            thesis.setStatus(status);
            masterThesisRepository.save(thesis);
        }
    }

    @Override
    public void setCommission(Long thesisId, String firstMember, String secondMember) {
        MasterThesis thesis = masterThesisRepository.findById(thesisId).orElse(null);
        if (thesis != null) {
            Professor mentor1 = this.professorRepository.findById(firstMember)
                    .orElseThrow(() -> new ProfessorDoesNotExistException(firstMember));
            Professor mentor2 = this.professorRepository.findById(secondMember)
                    .orElseThrow(() -> new ProfessorDoesNotExistException(secondMember));
            thesis.setFirstMember(mentor1);
            thesis.setSecondMember(mentor2);
            masterThesisRepository.save(thesis);

        }
    }

    @Override
    public void updateLocationAndDate(Long thesisId, String room, LocalDateTime time) {
        MasterThesis thesis = masterThesisRepository.findById(thesisId).orElse(null);
        if (thesis != null) {
            Room r = this.roomRepository.findById(room).orElseThrow(() -> new RoomDoesNotExistsException(room));
            MasterThesisPresentation masterThesisPresentation = new MasterThesisPresentation(room, time);
            thesis.setPresentation(masterThesisPresentation);
            masterThesisRepository.save(thesis);
        }
    }

    @Override
    public void updateOblast(Long thesisId, String oblast) {
        MasterThesis thesis = masterThesisRepository.findById(thesisId).orElse(null);
        if (thesis != null) {
            thesis.setArea(oblast);
            masterThesisRepository.save(thesis);
        }
    }



    @Override
    public void updateArchiveNumber(Long thesisId, String archiveNumber) {
        MasterThesis thesis = masterThesisRepository.findById(thesisId).orElse(null);
        if (thesis != null) {
            thesis.setArchiveNumber(archiveNumber);
            masterThesisRepository.save(thesis);
        }

    }

}
