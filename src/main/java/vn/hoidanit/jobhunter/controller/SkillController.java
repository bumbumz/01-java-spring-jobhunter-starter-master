package vn.hoidanit.jobhunter.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.Skill;
import vn.hoidanit.jobhunter.domain.Request.ResultPaginationDTO;
import vn.hoidanit.jobhunter.service.SkillService;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/v1")

public class SkillController {
    private final SkillService skillService;

    public SkillController(SkillService skillService) {
        this.skillService = skillService;

    }

    @PostMapping("/skills")
    public ResponseEntity<Skill> creatSkills(@Valid @RequestBody Skill param) throws IdInvalidException {
        Optional<Skill> checkSkill = this.skillService.checkName(param.getName());
        if (checkSkill.isPresent()) {
            throw new IdInvalidException("Skill đã tồn tại");

        }
        Skill res = this.skillService.creatSkills(param);

        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @PutMapping("/skills")
    public ResponseEntity<Skill> updateSkill(@Valid @RequestBody Skill param) throws IdInvalidException {
        Optional<Skill> checkSkill = this.skillService.checkId(param.getId());
        if (!checkSkill.isPresent()) {
            throw new IdInvalidException("Id không tồn tại");

        }
        Optional<Skill> checkName = this.skillService.checkName(param.getName());
        if (checkName.isPresent()) {
            throw new IdInvalidException("Tên đã  tồn tại");

        }
        Skill res = this.skillService.updateSkillsById(param);

        return ResponseEntity.ok().body(res);
    }

    @GetMapping("/skills")
    public ResponseEntity<?> fillterSkill(
            @Filter Specification<Skill> spec,
            Pageable page) {
        ResultPaginationDTO res = this.skillService.filterSkill(spec, page);
        return ResponseEntity.ok().body(res);
    }
    @DeleteMapping("/skills/{id}")
    public ResponseEntity<Void> deleteByid(@PathVariable("id")Long id) throws IdInvalidException
    {
        Optional<Skill> checkSkill = this.skillService.checkId(id);
        if (!checkSkill.isPresent()) {
            throw new IdInvalidException("Id không tồn tại");

        }
        this.skillService.delete(checkSkill.get());
        return ResponseEntity.ok().body(null);

    }

}
