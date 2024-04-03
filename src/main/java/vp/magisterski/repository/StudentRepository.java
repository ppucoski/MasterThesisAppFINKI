package vp.magisterski.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vp.magisterski.model.shared.Student;

import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, String> {
    //TODO: func
    Optional<Student> findByIndexStartingWith(String studentIndex);
}
