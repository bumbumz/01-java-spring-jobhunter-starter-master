package vn.hoidanit.jobhunter.service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.turkraft.springfilter.boot.Filter;

import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.Request.Meta;
import vn.hoidanit.jobhunter.domain.Request.ResUserDTO;
import vn.hoidanit.jobhunter.domain.Request.ResultPaginationDTO;
import vn.hoidanit.jobhunter.domain.dto.Request.RequestUserUpdate;
import vn.hoidanit.jobhunter.repository.UserRepository;
import vn.hoidanit.jobhunter.util.constant.GenderEnum;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User checkIdUser(long id) throws IdInvalidException {
        Optional<User> checkIdser = this.userRepository.findById(id);
        if (!checkIdser.isPresent()) {
            throw new IdInvalidException("id không tồn tại");
        }
        return checkIdser.get();
    }

    public ResUserDTO convertToResCreateUser(User user) {
        ResUserDTO resUserDTO = new ResUserDTO();

        resUserDTO.setId(user.getId());
        resUserDTO.setName(user.getName());
        resUserDTO.setAge(user.getAge());
        resUserDTO.setAddress(user.getAddress());
        resUserDTO.setEmail(user.getEmail());
        resUserDTO.setGender(user.getGender());

        resUserDTO.setCreatedAt(user.getCreatedAt());
        resUserDTO.setCreatedBy(user.getCreatedBy());

        return resUserDTO;

    }

    public ResUserDTO createUser(User user) throws IdInvalidException {

        boolean checkEmail = this.userRepository.existsByEmail(user.getEmail());
        if (checkEmail == true) {
            throw new IdInvalidException("Email đã tồn tại");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        User savedUser = userRepository.save(user);
        ResUserDTO resUserDTO = convertToResCreateUser(savedUser);

        return resUserDTO;
    }

    public RequestUserUpdate updateUser(RequestUserUpdate param) throws IdInvalidException {

        User user = this.checkIdUser(param.getId());
        user.setName(param.getName());
        user.setAge(param.getAge());
        user.setAddress(param.getAddress());
        user.setGender(param.getGender());

        this.userRepository.save(user);

        return param;

    }

    public ResultPaginationDTO getAllUsers(Specification<User> spec,
            Pageable pageale) {
        Page<User> pageUser = this.userRepository.findAll(spec, pageale);

        ResultPaginationDTO res = new ResultPaginationDTO();
        Meta meta = new Meta();

        meta.setPage(pageale.getPageNumber() + 1);
        meta.setPageSize(pageale.getPageSize());

        meta.setPages(pageUser.getTotalPages());
        meta.setTotal(pageUser.getNumberOfElements());

        res.setMeta(meta);
        List<ResUserDTO> resDTO = pageUser.getContent()
                .stream().map(m -> new ResUserDTO(
                        m.getId(),
                        m.getName(),
                        m.getEmail(),
                        m.getAge(),
                        m.getGender(),
                        m.getAddress(),
                        m.getCreatedAt(),
                        m.getCreatedBy()))
                .collect(Collectors.toList());
        res.setResult(pageUser.getContent());

        return res;
    }

    public List<User> getAll() {
        List<User> pageUser = this.userRepository.findAll();

        return pageUser;
    }

    public ResUserDTO getUser(Long id) throws IdInvalidException {
        Optional<User> user = userRepository.findById(id);
        if (!user.isPresent()) {
            throw new IdInvalidException("User not found");
        }
        ResUserDTO res = convertToResCreateUser(user.get());
        return res;

    }

    public void deleteUser(long id) throws IdInvalidException {
        this.getUser(id);

        userRepository.deleteById(id);

    }

    public Optional<User> handleGetUserByUsername(String name) {
        Optional<User> user = userRepository.findByEmail(name);

        return user;

    }

    public void updateTokenUser(String token, String email) {
        Optional<User> user = this.handleGetUserByUsername(email);
        if (user.isPresent()) {
            User resuser = user.get();
            resuser.setRefreshToken(token);
            this.userRepository.save(resuser);

        }
    }

    public User getUserbyRefreshTokenAndEmail(String token, String email)

    {
        return this.userRepository.findByRefreshTokenAndEmail(token, email);
    }

    public void logOut(User user) {
        user.setRefreshToken(null);
        this.userRepository.save(user);

    }

}
