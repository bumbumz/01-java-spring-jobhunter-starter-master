package vn.pcv.jobhunter.service;

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

import vn.pcv.jobhunter.domain.Company;
import vn.pcv.jobhunter.domain.Role;
import vn.pcv.jobhunter.domain.User;
import vn.pcv.jobhunter.domain.Request.CompanyinUser;
import vn.pcv.jobhunter.domain.Request.Meta;
import vn.pcv.jobhunter.domain.Request.ResUserDTO;
import vn.pcv.jobhunter.domain.Request.ResultPaginationDTO;
import vn.pcv.jobhunter.domain.Request.RoleInUser;
import vn.pcv.jobhunter.domain.dto.Request.RequestUserUpdate;
import vn.pcv.jobhunter.repository.CompanyRepository;
import vn.pcv.jobhunter.repository.UserRepository;
import vn.pcv.jobhunter.util.constant.GenderEnum;
import vn.pcv.jobhunter.util.error.IdInvalidException;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CompanyRepository companyRepository;
    private final RoleService roleService;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            CompanyRepository companyRepository,
            RoleService roleService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.companyRepository = companyRepository;
        this.roleService = roleService;
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
        resUserDTO.setCompany(
                user.getCompany() != null
                        ? new CompanyinUser(user.getCompany().getId(), user.getCompany().getName())
                        : null);
        resUserDTO.setRole(
                user.getRole() != null
                        ? new RoleInUser(user.getRole().getId(), user.getRole().getName())
                        : null);

        return resUserDTO;

    }

    public ResUserDTO createUser(User user) throws IdInvalidException {

        boolean checkEmail = this.userRepository.existsByEmail(user.getEmail());
        if (checkEmail == true) {
            throw new IdInvalidException("Email đã tồn tại");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getCompany() != null) {
            Optional<Company> company = this.companyRepository.findById(user.getCompany().getId());

            user.setCompany(company.isPresent() ? company.get() : null);

        
        }
        if(user.getRole()!=null)

        {
            Optional<Role> checkId= this.roleService.checkId( user.getRole().getId());
            user.setRole(checkId.isPresent()?checkId.get():null);
            
        }
        User savedUser = userRepository.save(user);

        ResUserDTO resUserDTO = convertToResCreateUser(savedUser);

        return resUserDTO;
    }


    public ResUserDTO updateUser(RequestUserUpdate param) throws IdInvalidException {

        User user = this.checkIdUser(param.getId());
        if (param.getName() != null) {
            user.setName(param.getName());
        }

        user.setAge(param.getAge());
        user.setAddress(param.getAddress());
        user.setGender(param.getGender());
        if (param.getCompany() != null) {
            Optional<Company> company = this.companyRepository.findById(param.getCompany().getId());
            if (!company.isPresent()) {
                user.setCompany(null);
            } else {
                user.setCompany(company.get());

            }

        }

        if (param.getRole() != null) {
            Optional<Role> checkId = this.roleService.checkId(param.getRole().getId());
            user.setRole(checkId.isPresent() ? checkId.get() : null);
        }
        this.userRepository.save(user);
        ResUserDTO resUserDTO = convertToResCreateUser(user);
        return resUserDTO;

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
                .stream().map(m -> this.convertToResCreateUser(m

                )).collect(Collectors.toList());
        res.setResult(resDTO);

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
