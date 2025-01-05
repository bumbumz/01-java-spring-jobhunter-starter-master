package vn.hoidanit.jobhunter.controller;

import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.dto.ResultPaginationDTO;
import vn.hoidanit.jobhunter.service.CompanyService;
import vn.hoidanit.jobhunter.util.annotation.AppMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@RestController
@RequestMapping("/api/v1")
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @PostMapping("/companies")
    public ResponseEntity<Company> handlePrepersistCompany(@Valid @RequestBody Company param)
            throws IdInvalidException {

        return ResponseEntity.status(HttpStatus.CREATED).body(this.companyService.create(param));
    }

    // @GetMapping("/companies")
    // public ResponseEntity<?> handleGetAll(
    // @RequestParam("current") Optional<String> currentOptional,
    // @RequestParam("pageSize") Optional<String> pageSizeOptional

    // ) {
    // String isCurrent = currentOptional.isPresent() == true ?
    // currentOptional.get() : null;
    // String isPageSize = pageSizeOptional.isPresent() == true ?
    // pageSizeOptional.get() : null;
    // if (isCurrent != null && isPageSize != null) {
    // Pageable pageable = PageRequest.of(Integer.parseInt(isCurrent) - 1,
    // Integer.parseInt(isPageSize));
    // return ResponseEntity.ok().body(this.companyService.getAll(pageable));
    // }
    // return ResponseEntity.ok().body(this.companyService.getAll());

    // }

    @GetMapping("/companies")
    @AppMessage("dữ liệu tìm ra là")
    public ResponseEntity<?> handleFiter(
            @Filter Specification<Company> spec,
            Pageable pageable
    ) {
        return ResponseEntity.ok().body(this.companyService.getFiter(spec,pageable));

    }

    @GetMapping("/companies/{id}")
    public ResponseEntity<Company> handleGetBYId(@PathVariable("id") long id) throws IdInvalidException {
        return ResponseEntity.ok().body(this.companyService.getByid(id));
    }

    @PutMapping("/companies")
    public ResponseEntity<Company> handleUpdate(@Valid @RequestBody Company param)
            throws IdInvalidException {
        return ResponseEntity.ok().body(this.companyService.updateByid(param));
    }

    @DeleteMapping("/companies/{id}")
    public ResponseEntity<String> handleRemove(@PathVariable("id") long id) throws IdInvalidException {
        this.companyService.removeByid(id);
        return ResponseEntity.ok().body("Xóa thành công");
    }

}
