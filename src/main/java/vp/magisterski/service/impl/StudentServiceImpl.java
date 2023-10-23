package vp.magisterski.service.impl;

import org.springframework.stereotype.Service;
import vp.magisterski.model.shared.Student;
import vp.magisterski.repository.StudentRepository;
import vp.magisterski.service.StudentService;

import java.util.Optional;

@Service
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;

    public StudentServiceImpl(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Override
    public Optional<Student> findStudentById(String studentId) {
        if (studentId == null) {
            return Optional.empty();
        } else
            return studentRepository.findById(studentId);
    }
}