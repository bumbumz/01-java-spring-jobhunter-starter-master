package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.repository.UserRepository;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);
        return savedUser;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUser(Long id) throws IdInvalidException {
        Optional<User> user = userRepository.findById(id);
        if (!user.isPresent()) {
            throw new IdInvalidException("User not found");
        }
        return user.get();

    }

    public void deleteUser(long id) throws IdInvalidException {
        this.getUser(id);

        userRepository.deleteById(id);

    }

    public User handleGetUserByUsername(String name)
    {
        User user = userRepository.findByEmail(name);
        return user;

    }
}
