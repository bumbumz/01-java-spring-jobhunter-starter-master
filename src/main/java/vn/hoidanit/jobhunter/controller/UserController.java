package vn.hoidanit.jobhunter.controller;

import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.dto.ResUserDTO;
import vn.hoidanit.jobhunter.domain.dto.ResultPaginationDTO;
import vn.hoidanit.jobhunter.domain.dto.Request.RequestUserUpdate;
import vn.hoidanit.jobhunter.service.UserService;
import vn.hoidanit.jobhunter.util.annotation.AppMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/v1")

public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/users")
    @AppMessage("Tạo người dùng thành công")
    public ResponseEntity<ResUserDTO> creatUser(@Valid @RequestBody User userParam) throws IdInvalidException {

        ResUserDTO user = userService.createUser(userParam);

        return ResponseEntity.ok().body(user);
    }

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers(
            @Filter Specification<User> spec,
            Pageable pageale) {

        ResultPaginationDTO users = userService.getAllUsers(spec, pageale);
        return ResponseEntity.ok().body(users);

    }

    @PutMapping("/users")
    public ResponseEntity<?> updateUser(@Valid @RequestBody RequestUserUpdate param) throws IdInvalidException {

        RequestUserUpdate users = userService.updateUser(param);
        return ResponseEntity.ok().body(users);

    }

    @GetMapping("/users/{id}")
    @AppMessage("Lấy dữ liệu thành công")
    public ResponseEntity<ResUserDTO> getMethodName(@PathVariable("id") Long id) throws IdInvalidException {
        return ResponseEntity.ok().body(userService.getUser(id));

    }

    // @GetMapping("/users/{id}")
    // public User getMethodName(@PathVariable("id") Long id) {
    // return userService.getUser(id);

    // }
    @DeleteMapping("/users/{id}")
    @AppMessage("Xóa người dùng thành công")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") Long id) throws IdInvalidException {
        userService.deleteUser(id);
        return ResponseEntity.ok().body(null);
    }

}
