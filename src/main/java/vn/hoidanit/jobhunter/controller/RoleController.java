package vn.hoidanit.jobhunter.controller;

import java.util.Optional;

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
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.Role;
import vn.hoidanit.jobhunter.service.RoleService;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class RoleController {
    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;

    }

    @PostMapping("/roles")
    public ResponseEntity<Role> createRole(@Valid @RequestBody Role param) throws IdInvalidException {
        Optional<Role> checkName = this.roleService.checkName(param.getName());
        if (checkName.isPresent()) {
            throw new IdInvalidException("Tên Role đã tồn tại ");
        }
        return ResponseEntity.ok().body(this.roleService.createRole(param));

    }

    @PutMapping("/roles")
    public ResponseEntity<Role> updateRole(@RequestBody Role param) throws IdInvalidException {
        Optional<Role> checkId = this.roleService.checkId(param.getId());
        if (!checkId.isPresent()) {
            throw new IdInvalidException("Role " + param.getId() + " ko tồn tại ");
        }
        // Optional<Role> checkName = this.roleService.checkName(param.getName());
        // if (checkName.isPresent() &&
        // !checkName.get().getName().equals(checkId.get().getName())) {
        // throw new IdInvalidException("Tên Role đã tồn tại ");
        // }
        return ResponseEntity.ok().body(this.roleService.updateRole(param));

    }

    @GetMapping("/roles")
    public ResponseEntity<?> filterRole(
            @Filter Specification<Role> spec,
            Pageable pageable) {
        return ResponseEntity.ok().body(this.roleService.filter(spec, pageable));
    }

    @DeleteMapping("/roles/{id}")
    public ResponseEntity<Void> deleteByid(@PathVariable("id") long id) throws IdInvalidException {
        Optional<Role> checkId = this.roleService.checkId(id);
        if (!checkId.isPresent()) {
            throw new IdInvalidException("Role " + id + " ko tồn tại ");
        }
        this.roleService.delleteByid(id);
        return ResponseEntity.ok().body(null);

    }

    @GetMapping("/roles/{id}")
    public ResponseEntity<Role> getById( @PathVariable("id") long id) throws IdInvalidException
    {
        Optional<Role> checkId = this.roleService.checkId(id);
        if (!checkId.isPresent()) {
            throw new IdInvalidException("Role " + id + " ko tồn tại ");
        }
        return ResponseEntity.ok().body(checkId.get());

    }
}
