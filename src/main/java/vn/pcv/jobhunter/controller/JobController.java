package vn.pcv.jobhunter.controller;

import java.nio.file.OpenOption;
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
import vn.pcv.jobhunter.domain.Job;
import vn.pcv.jobhunter.domain.User;
import vn.pcv.jobhunter.domain.Request.ResultPaginationDTO;
import vn.pcv.jobhunter.domain.dto.Request.ReponseJobCreatDTO;
import vn.pcv.jobhunter.service.JobService;
import vn.pcv.jobhunter.util.error.IdInvalidException;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/v1")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;

    }

    @PostMapping("/jobs")
    public ResponseEntity<ReponseJobCreatDTO> createJob(@Valid @RequestBody Job param) throws IdInvalidException {
        return ResponseEntity.ok().body(this.jobService.createJob(param));

    }

    @PutMapping("/jobs")
    public ResponseEntity<ReponseJobCreatDTO> updateJobs(@RequestBody Job param) throws IdInvalidException {

        Optional<Job> checkId = this.jobService.checkId(param.getId());
        if (!checkId.isPresent()) {
            throw new IdInvalidException("Id không tồn tại");
        }
        return ResponseEntity.ok().body(this.jobService.updateJobs(param));

    }

    @DeleteMapping("/jobs/{id}")
    public ResponseEntity<Void> deleteId(@PathVariable("id") long id) throws IdInvalidException {
        Optional<Job> checkId = this.jobService.checkId(id);
        if (!checkId.isPresent()) {
            throw new IdInvalidException("Id không tồn tại");
        }
        this.jobService.deleteById(id);
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/jobs")
    public ResponseEntity<?> filterJobs(
            @Filter Specification<Job> spec,
            Pageable pageale) {
        ResultPaginationDTO res = this.jobService.filterJob(spec, pageale);

        return ResponseEntity.ok().body(res);
    }

    @GetMapping("/jobs/{id}")
    public ResponseEntity<Job> getByid(@PathVariable("id") long id) throws IdInvalidException {
        Optional<Job> checkId = this.jobService.checkId(id);
        if (!checkId.isPresent()) {
            throw new IdInvalidException("Id không tồn tại");
        }
        return ResponseEntity.ok().body(checkId.get());
    }

}
