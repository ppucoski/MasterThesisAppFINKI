package vp.magisterski.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vp.magisterski.model.shared.User;


public interface UserRepository extends JpaRepository<User, String> {

    //TODO: func
}
