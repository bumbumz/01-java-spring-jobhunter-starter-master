package vn.pcv.jobhunter.service;

import java.nio.file.OpenOption;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.pcv.jobhunter.domain.Permission;
import vn.pcv.jobhunter.domain.Role;
import vn.pcv.jobhunter.domain.Request.Meta;
import vn.pcv.jobhunter.domain.Request.ResultPaginationDTO;
import vn.pcv.jobhunter.repository.PermissionRepository;
import vn.pcv.jobhunter.repository.RoleRepository;

@Service
public class RoleService {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public RoleService(RoleRepository roleRepository,
            PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    public Role createRole(Role param) {
        Role res = new Role();
        res.setName(param.getName());
        res.setDescription(param.getDescription());
        res.setActive(param.isActive());

        if (param.getPermissions() != null) {
            List<Long> listid = param.getPermissions().stream().map(
                    item -> item.getId()).collect(Collectors.toList());
            List<Permission> permission = this.permissionRepository.findByIdIn(listid);
            res.setPermissions(permission);
        }

        return this.roleRepository.save(res);

    }

    public Role updateRole(Role param) {
        Role checkId = this.checkId(param.getId()).get();
        if (param.getName() != null) {
            checkId.setName(param.getName());

        }
        if (param.getDescription() != null) {
            checkId.setDescription(param.getDescription());
        }
        if (param.getPermissions() != null) {
            List<Long> listid = param.getPermissions().stream().map(
                    item -> item.getId()).collect(Collectors.toList());
            List<Permission> permission = this.permissionRepository.findByIdIn(listid);
            checkId.setPermissions(permission);

        }
        return this.roleRepository.save(checkId);

    }

    public ResultPaginationDTO filter(Specification<Role> spec,
            Pageable pageable) {
        Page<Role> pageRole = this.roleRepository.findAll(spec, pageable);
        ResultPaginationDTO res = new ResultPaginationDTO();
        Meta meta = new Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(pageRole.getTotalPages());
        meta.setTotal(pageRole.getTotalElements());

        res.setMeta(meta);
        res.setResult(pageRole.getContent());

        return res;
    }

    public void delleteByid(long id) {
        this.roleRepository.deleteById(id);
    }

    // check=======================================================================================
    public Optional<Role> checkName(String name) {
        return this.roleRepository.findByName(name);
    }

    public Optional<Role> checkId(long id) {
        return this.roleRepository.findById(id);
    }
}
