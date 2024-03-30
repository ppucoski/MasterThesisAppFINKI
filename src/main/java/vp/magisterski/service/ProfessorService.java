package vp.magisterski.service;

import vp.magisterski.model.shared.Professor;

import java.util.List;
import java.util.Optional;

public interface ProfessorService {
    List<Professor> findAll();
    List<Professor> findAllByProfessorStatus(Boolean isProfessor, Boolean canBeElected);
    Professor findProfessorByName(String name);
    Optional<Professor> findProfessorById(String id);


}
