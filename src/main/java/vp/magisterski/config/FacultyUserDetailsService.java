package vp.magisterski.config;

import vp.magisterski.model.exceptions.InvalidUsernameException;
import vp.magisterski.model.exceptions.ProfessorDoesNotExistException;
import vp.magisterski.model.shared.Professor;
import vp.magisterski.model.shared.Student;
import vp.magisterski.model.shared.User;
import vp.magisterski.model.shared.UserRole;
import vp.magisterski.repository.StudentRepository;
import vp.magisterski.repository.UserRepository;
import vp.magisterski.service.ProfessorService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class FacultyUserDetailsService implements UserDetailsService {

    @Value("${system.authentication.password}")
    String systemAuthenticationPassword;

    final UserRepository userRepository;

    final ProfessorService professorService;

    final PasswordEncoder passwordEncoder;

    final StudentRepository studentRepository;

    public FacultyUserDetailsService(UserRepository userRepository,
                                     ProfessorService professorService,
                                     PasswordEncoder passwordEncoder,
                                     StudentRepository studentRepository) {
        this.userRepository = userRepository;
        this.professorService = professorService;
        this.passwordEncoder = passwordEncoder;
        this.studentRepository = studentRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findById(username).orElse(null);
        if (user == null) {
            Student student = studentRepository.findById(username).orElseThrow(InvalidUsernameException::new);
            User user1 = new User();
            user1.setId(username);
            user1.setRole(UserRole.STUDENT);
            return new FacultyUserDetails(user1, student, passwordEncoder.encode(systemAuthenticationPassword));
        } else if (user.getRole().isProfessor()) {
            Professor professor = professorService.findProfessorById(username).orElseThrow(() -> new ProfessorDoesNotExistException(username));
            return new FacultyUserDetails(user, professor, passwordEncoder.encode(systemAuthenticationPassword));
        } else {
            return new FacultyUserDetails(user, passwordEncoder.encode(systemAuthenticationPassword));
        }
    }
}
