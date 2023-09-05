package vp.magisterski.service.impl;

import org.springframework.stereotype.Service;
import vp.magisterski.model.shared.Professor;
import vp.magisterski.service.ProfessorService;

import java.util.List;
import java.util.Optional;

@Service
public class ProfessorServiceImpl implements ProfessorService {
    @Override
    public List<Professor> findAll() {
        return null;
    }

    @Override
    public List<Professor> findAllByProfessorStatus(Boolean isProfessor, Boolean canBeElected) {
        return null;
    }

    @Override
    public Optional<Professor> findProfessorById(String id) {
        return Optional.empty();
    }

    @Override
    public List<Professor> findAllMentors() {
        return null;
    }

    @Override
    public List<Professor> findAllMembers() {
        return null;
    }
}
