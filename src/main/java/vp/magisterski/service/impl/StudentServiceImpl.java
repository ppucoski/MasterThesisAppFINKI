package vp.magisterski.service.impl;

import org.springframework.stereotype.Service;
import vp.magisterski.model.shared.Student;
import vp.magisterski.service.StudentService;

import java.util.Optional;

@Service
public class StudentServiceImpl implements StudentService {
    @Override
    public Optional<Student> findStudent(String student) {
        return Optional.empty();
    }
}
