package vp.magisterski.service;

import vp.magisterski.model.shared.Student;

import java.util.Optional;

public interface UserService {
    Optional<Student> findUser(String user);
}
