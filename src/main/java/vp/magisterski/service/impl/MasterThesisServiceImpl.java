package vp.magisterski.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vp.magisterski.model.magister.MasterThesis;
import vp.magisterski.model.shared.Student;
import vp.magisterski.repository.MasterThesisRepository;
import vp.magisterski.repository.StudentRepository;
import vp.magisterski.service.MasterThesisService;

import java.util.Optional;

@Service
public class MasterThesisServiceImpl implements MasterThesisService {

    private final MasterThesisRepository masterThesisRepository;
    private final StudentRepository studentRepository;

    public MasterThesisServiceImpl(MasterThesisRepository masterThesisRepository, StudentRepository studentRepository) {
        this.masterThesisRepository = masterThesisRepository;
        this.studentRepository = studentRepository;
    }

    @Override
    public MasterThesis save(String index, String title, String area, String description, String mentor, String firstMember, String secondMember) {
        //TODO: opcija broj dva, da se kreira posebna exception klasa InvalidStudentByIndex koja ke mozeme da ja iskoristime dokolku
        // ne postoi student so takov indeks, pri shto ke iskoristime orElseThrow
        Student student = this.studentRepository.findByIndex(index).orElse(null);
        //TODO: da se napravi nov MasterThesis objekt koj ponataka ke bide zacuvan
        //MasterThesis master = new MasterThesis(student, title, area, description, mentor, firstMember, secondMember);
        return null;
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
