package vp.magisterski.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vp.magisterski.model.shared.Student;

public interface StudentRepository extends JpaRepository<Student, String> {
    //TODO: func
}
