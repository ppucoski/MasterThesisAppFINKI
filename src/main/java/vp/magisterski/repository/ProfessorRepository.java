package vp.magisterski.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vp.magisterski.model.shared.Professor;

import java.util.List;

public interface ProfessorRepository extends JpaRepository<Professor, String> {

    //TODO: func
}
