package vp.magisterski.service.impl;

import org.springframework.stereotype.Service;
import vp.magisterski.model.shared.Student;
import vp.magisterski.service.UserService;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    @Override
    public Optional<Student> findUser(String user) {
        return Optional.empty();
    }
}
