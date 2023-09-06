package vp.magisterski.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vp.magisterski.model.exceptions.ProfessorDoesNotExistException;
import vp.magisterski.model.exceptions.StudentDoesNotExistException;
import vp.magisterski.model.magister.MasterThesis;
import vp.magisterski.model.magister.MasterThesisStatus;
import vp.magisterski.model.shared.Professor;
import vp.magisterski.model.shared.Student;
import vp.magisterski.repository.MasterThesisRepository;
import vp.magisterski.repository.ProfessorRepository;
import vp.magisterski.repository.StudentRepository;
import vp.magisterski.service.MasterThesisService;

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
    public MasterThesis save(String studentIndex, String title, String area, String description, String mentorId, String firstMemberId, String secondMemberId) {
        Student student = this.studentRepository.findById(studentIndex)
                .orElseThrow(() -> new StudentDoesNotExistException(studentIndex));
        Professor mentor = this.professorRepository.findById(mentorId)
                .orElseThrow(() -> new ProfessorDoesNotExistException(mentorId));
        Professor firstMember = this.professorRepository.findById(firstMemberId)
                .orElseThrow(() -> new ProfessorDoesNotExistException(firstMemberId));
        Professor secondMember = this.professorRepository.findById(secondMemberId)
                .orElseThrow(() -> new ProfessorDoesNotExistException(secondMemberId));
        MasterThesis masterThesis = new MasterThesis(MasterThesisStatus.PROFESSOR_THESIS_REGISTRATION,
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
}
