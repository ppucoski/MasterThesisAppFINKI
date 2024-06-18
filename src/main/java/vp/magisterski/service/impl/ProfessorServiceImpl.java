package vp.magisterski.service.impl;

import org.springframework.stereotype.Service;
import vp.magisterski.model.shared.Professor;
import vp.magisterski.repository.ProfessorRepository;
import vp.magisterski.service.ProfessorService;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProfessorServiceImpl implements ProfessorService {
    private final ProfessorRepository professorRepository;

    public ProfessorServiceImpl(ProfessorRepository professorRepository) {
        this.professorRepository = professorRepository;
    }

    @Override
    public List<Professor> findAll() {
        return this.professorRepository.findAll();
    }


    @Override
    public List<Professor> findAllByProfessorStatus(Boolean isProfessor, Boolean canBeElected) {
        return professorRepository.findAll()
                .stream()
                .filter(professor -> professor.getTitle().isProfessor() &&
                        (canBeElected || !professor.getTitle().name().startsWith("ELECTED")))
                .sorted(Comparator.comparing(Professor::getName))
                .collect(Collectors.toList());
    }

    @Override
    public Professor findProfessorByName(String name) {
        return this.professorRepository.findProfessorByName(name);
    }

    @Override
    public Optional<Professor> findProfessorById(String id) {
        if (id == null) {
            return Optional.empty();
        } else
            return this.professorRepository.findById(id);
    }

}
