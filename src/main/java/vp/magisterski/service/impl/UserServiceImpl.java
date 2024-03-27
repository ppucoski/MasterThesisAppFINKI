package vp.magisterski.service.impl;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vp.magisterski.model.shared.Student;
import vp.magisterski.model.shared.User;
import vp.magisterski.repository.StudentRepository;
import vp.magisterski.repository.UserRepository;
import vp.magisterski.service.UserService;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;

    public UserServiceImpl(UserRepository userRepository, StudentRepository studentRepository) {
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
    }

    @Override
    public Optional<User> findUserById(String id) {
        return this.userRepository.findById(id);
    }

    @Transactional
    @Override
    public String getUsernameFromUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !authentication.getPrincipal().equals("anonymousUser")) {
            String username = authentication.getName();
            User user = userRepository.findById(username).orElse(null);
            if (user != null) {
                return user.getName();
            } else {
                Student student = studentRepository.findById(username).orElse(null);
                if (student != null) {
                    return student.getIndex();
                }
            }
        }
        return null;
    }

    @Override
    public User getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !authentication.getPrincipal().equals("anonymousUser")) {
            String username = authentication.getName();
            return userRepository.findById(username).orElse(null);
        }
        return null;
    }
}