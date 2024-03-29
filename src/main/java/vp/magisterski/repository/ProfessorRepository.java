package vp.magisterski.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vp.magisterski.model.shared.Professor;

public interface ProfessorRepository extends JpaRepository<Professor, String> {
    //TODO: func
    Professor findProfessorByName(String name);
}
