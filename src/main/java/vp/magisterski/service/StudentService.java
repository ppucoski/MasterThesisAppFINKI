package vp.magisterski.service;

import vp.magisterski.model.shared.Student;

import java.util.Optional;

public interface StudentService {
    Optional<Student> findStudentById(String studentId);
}
