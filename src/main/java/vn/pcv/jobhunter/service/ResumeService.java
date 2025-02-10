package vn.pcv.jobhunter.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.turkraft.springfilter.converter.FilterSpecification;
import com.turkraft.springfilter.converter.FilterSpecificationConverter;
import com.turkraft.springfilter.parser.FilterParser;
import com.turkraft.springfilter.parser.node.FilterNode;

import vn.pcv.jobhunter.domain.Company;
import vn.pcv.jobhunter.domain.Job;
import vn.pcv.jobhunter.domain.Resume;
import vn.pcv.jobhunter.domain.User;
import vn.pcv.jobhunter.domain.Request.Meta;
import vn.pcv.jobhunter.domain.Request.ResultPaginationDTO;
import vn.pcv.jobhunter.domain.dto.Resume.JobInGetById;
import vn.pcv.jobhunter.domain.dto.Resume.ReponseGetByIdDTO;
import vn.pcv.jobhunter.domain.dto.Resume.ReponseResmeDTO;
import vn.pcv.jobhunter.domain.dto.Resume.ReponseUpResumeDTO;
import vn.pcv.jobhunter.domain.dto.Resume.UserInGetId;
import vn.pcv.jobhunter.repository.ResumeRepository;
import vn.pcv.jobhunter.util.error.IdInvalidException;

@Service
public class ResumeService {
    @Autowired
    private FilterParser filterParser;

    @Autowired
    private FilterSpecificationConverter filterSpecificationConverter;
    private final ResumeRepository resumeRepository;
    private final UserService userService;
    private final JobService jobService;

    public ResumeService(ResumeRepository resumeRepository,
            UserService userService,
            JobService jobService) {
        this.resumeRepository = resumeRepository;
        this.userService = userService;
        this.jobService = jobService;
    }

    private ReponseGetByIdDTO mapperReponseGetByIdDTOToResume(Resume param) {
        ReponseGetByIdDTO res = new ReponseGetByIdDTO(
                param.getId(),
                param.getEmail(),
                param.getUrl(),
                param.getStatus(),
                param.getJob().getCompany().getName(),
                param.getCreatedAt(),
                param.getCreatedBy(),
                param.getUpdatedAt(),
                param.getUpdatedBy(),

                new UserInGetId(param.getUser().getId(), param.getUser().getName()),
                new JobInGetById(param.getJob().getId(), param.getUser().getName())

        );
        return res;
    }

    public ReponseResmeDTO createResume(Resume param) throws IdInvalidException {
        Resume resume = new Resume();
        resume.setEmail(param.getEmail());
        resume.setUrl(param.getUrl());
        resume.setStatus(param.getStatus());
        User user = this.userService.checkIdUser(param.getUser().getId());
        Job job = this.jobService.checkId(param.getJob().getId()).get();
        resume.setUser(user);
        resume.setJob(job);
        this.resumeRepository.save(resume);
        ReponseResmeDTO res = new ReponseResmeDTO(
                resume.getId(),
                resume.getCreatedAt(),
                resume.getUpdatedAt(),
                resume.getCreatedBy(),
                resume.getUpdatedBy());
        return res;

    }

    public ReponseUpResumeDTO updateResume(Resume param) {
        Resume upresume = this.checkid(param.getId()).get();
        upresume.setStatus(param.getStatus());
        this.resumeRepository.save(upresume);
        ReponseUpResumeDTO res = new ReponseUpResumeDTO(
                upresume.getUpdatedAt(),
                upresume.getUpdatedBy());
        return res;
    }

    public ReponseGetByIdDTO getByid(Resume param) {
        ReponseGetByIdDTO res = this.mapperReponseGetByIdDTOToResume(param);
        return res;

    }

    public ResultPaginationDTO getFiter(Specification<Resume> spec, Pageable pageable) {
        Page<Resume> pageCompany = this.resumeRepository.findAll(spec, pageable);
        ResultPaginationDTO res = new ResultPaginationDTO();
        Meta meta = new Meta();
        meta.setPage(pageable.getPageNumber() + 1);// số trang lấy từ param url
        meta.setPageSize(pageable.getPageSize());// số lượng phần tử lấy từ param url

        meta.setPages(pageCompany.getTotalPages());// tổng số trang
        meta.setTotal(pageCompany.getTotalElements());// tổng số phần tử
        res.setMeta(meta);
        List<ReponseGetByIdDTO> listres = pageCompany.getContent().stream()
                .map(
                        item -> this.mapperReponseGetByIdDTOToResume(item))
                .collect(Collectors.toList());
        res.setResult(listres);
        return res;
    }

    public ResultPaginationDTO filterByUser(Pageable pageable, String email) {
        FilterNode node = filterParser.parse("email='" +                          email + "'");
        FilterSpecification<Resume> spec = filterSpecificationConverter.convert(node);
        Page<Resume> resumbyUser = this.resumeRepository.findAll(spec, pageable);
        ResultPaginationDTO res = new ResultPaginationDTO();
        Meta meta = new Meta();
        meta.setPage(pageable.getPageNumber() + 1);// số trang lấy từ param url
        meta.setPageSize(pageable.getPageSize());// số lượng phần tử lấy từ param url

        meta.setPages(resumbyUser.getTotalPages());// tổng số trang
        meta.setTotal(resumbyUser.getTotalElements());// tổng số phần tử
        res.setMeta(meta);
        List<ReponseGetByIdDTO> listres = resumbyUser.getContent().stream()
                .map(
                        item -> this.mapperReponseGetByIdDTOToResume(item))
                .collect(Collectors.toList());
        res.setResult(listres);
        return res;

    }

    public void deleteResume(long id) {
        this.resumeRepository.deleteById(id);
    }

    // check

    public Optional<Resume> checkid(long id) {
        return this.resumeRepository.findById(id);
    }

}
