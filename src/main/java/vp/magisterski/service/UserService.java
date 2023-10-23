package vp.magisterski.service;

import vp.magisterski.model.shared.User;

import java.util.Optional;

public interface UserService {
    Optional<User> findUserById(String id);
}
