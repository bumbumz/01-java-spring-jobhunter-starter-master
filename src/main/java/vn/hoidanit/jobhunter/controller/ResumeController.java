package vn.hoidanit.jobhunter.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;
import com.turkraft.springfilter.builder.FilterBuilder;
import com.turkraft.springfilter.converter.FilterSpecificationConverter;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.Job;
import vn.hoidanit.jobhunter.domain.Resume;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.dto.Resume.ReponseGetByIdDTO;
import vn.hoidanit.jobhunter.domain.dto.Resume.ReponseResmeDTO;
import vn.hoidanit.jobhunter.domain.dto.Resume.ReponseUpResumeDTO;
import vn.hoidanit.jobhunter.service.JobService;
import vn.hoidanit.jobhunter.service.ResumeService;
import vn.hoidanit.jobhunter.service.TokenService;
import vn.hoidanit.jobhunter.service.UserService;
import vn.hoidanit.jobhunter.util.annotation.AppMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/v1")
public class ResumeController {
    private final ResumeService resumeService;
    private final JobService jobService;
    private final UserService userService;
    @Autowired
    FilterBuilder fb;
    @Autowired
    FilterSpecificationConverter fsc;

    public ResumeController(ResumeService resumeService,
            JobService jobService,
            UserService userService) {
        this.resumeService = resumeService;
        this.jobService = jobService;
        this.userService = userService;
    }

    @PostMapping("/resumes")
    public ResponseEntity<?> createResume(@Valid @RequestBody Resume param) throws IdInvalidException {
        Optional<Job> job = this.jobService.checkId(param.getJob().getId());
        if (!job.isPresent()) {
            throw new IdInvalidException("id Job không tồn tại");
        }
        ReponseResmeDTO res = this.resumeService.createResume(param);
        return ResponseEntity.ok().body(res);

    }

    @PutMapping("/resumes")
    public ResponseEntity<?> updateResume(@RequestBody Resume param)
            throws IdInvalidException {
        Optional<Resume> checkid = this.resumeService.checkid(param.getId());
        if (!checkid.isPresent()) {
            throw new IdInvalidException("Không tìm thấy id này");
        }
        ReponseUpResumeDTO res = this.resumeService.updateResume(param);

        return ResponseEntity.ok().body(res);
    }

    @DeleteMapping("/resumes/{id}")
    public ResponseEntity<Void> deleteResume(@PathVariable("id") Long id) throws IdInvalidException {
        Optional<Resume> checkid = this.resumeService.checkid(id);
        if (!checkid.isPresent()) {
            throw new IdInvalidException("Không tìm thấy id này");
        }
        this.resumeService.deleteResume(id);
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/resumes/{id}")
    public ResponseEntity<ReponseGetByIdDTO> getByid(@PathVariable("id") Long id) throws IdInvalidException {
        Optional<Resume> checkid = this.resumeService.checkid(id);
        if (!checkid.isPresent()) {
            throw new IdInvalidException("Không tìm thấy id này");
        }
        ReponseGetByIdDTO res = this.resumeService.getByid(checkid.get());
        return ResponseEntity.ok().body(res);
    }

    @GetMapping("/resumes")
    @AppMessage("dữ liệu tìm ra là")
    public ResponseEntity<?> handleFiter(
            @Filter Specification<Resume> spec,
            Pageable pageable) {
        List<Long> arrJobs = null;
        String email = TokenService.getCurrentUserLogin().isPresent() == true ? TokenService.getCurrentUserLogin().get()
                : "";

        Optional<User> checkemail = this.userService.handleGetUserByUsername(email);
        if (checkemail.isPresent()) {
            Company company = checkemail.get().getCompany();
            if (company != null) {
                List<Job> jobincompany = company.getJobs();
                if (jobincompany != null && jobincompany.size() > 0) {
                    arrJobs = jobincompany.stream().map(x -> x.getId()).collect(Collectors.toList());

                }
            }
        }
        Specification<Resume> jobInSpec = fsc.convert(fb.field("job").in(fb.input(arrJobs)).get());
        Specification finalSpec = jobInSpec.and(spec);
        return ResponseEntity.ok().body(this.resumeService.getFiter(finalSpec, pageable));

    }

    @PostMapping("/resumes/by-user")
    public ResponseEntity<?> resumesByid(
            Pageable pageable) {
        String email = TokenService.getCurrentUserLogin().isPresent() ? TokenService.getCurrentUserLogin().get() : "";
        return ResponseEntity.ok().body(this.resumeService.filterByUser(pageable, email));
    }

}
