package vn.pcv.jobhunter.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.pcv.jobhunter.controller.SkillController;
import vn.pcv.jobhunter.domain.Company;
import vn.pcv.jobhunter.domain.Job;
import vn.pcv.jobhunter.domain.Skill;
import vn.pcv.jobhunter.domain.User;
import vn.pcv.jobhunter.domain.Request.Meta;
import vn.pcv.jobhunter.domain.Request.ResultPaginationDTO;
import vn.pcv.jobhunter.domain.dto.Request.ReponseJobCreatDTO;
import vn.pcv.jobhunter.repository.CompanyRepository;
import vn.pcv.jobhunter.repository.JobRepository;
import vn.pcv.jobhunter.repository.SkillRepository;
import vn.pcv.jobhunter.util.error.IdInvalidException;

@Service
public class JobService {

    private final JobRepository jobRepository;

    private final SkillRepository skillRepository;
    private final CompanyRepository companyRepository;

    public JobService(JobRepository jobRepository,
            SkillRepository skillRepository,
            CompanyRepository companyRepository) {
        this.jobRepository = jobRepository;
        this.skillRepository = skillRepository;
        this.companyRepository = companyRepository;

    }

    private ReponseJobCreatDTO mapperReponseJobCreatDTO_To_Job(Job newJob) {
        ReponseJobCreatDTO res = new ReponseJobCreatDTO();
        res.setId(newJob.getId());

        res.setName(newJob.getName());
        res.setLocation(newJob.getLocation());
        res.setSalary(newJob.getSalary());
        res.setLevel(newJob.getLevel());
        res.setDescription(newJob.getDescription());
        res.setStartDate(newJob.getStartDate());
        res.setEndDate(newJob.getEndDate());
        if (newJob.getSkills() != null) {
            List<String> skillsListString = newJob.getSkills().stream()
                    .map(
                            skills -> skills.getName())
                    .collect(Collectors.toList());
            res.setSkills(skillsListString);

        } else {
            res.setSkills(null);
        }
        return res;
    }

    public ReponseJobCreatDTO createJob(Job param) throws IdInvalidException {
        if (param.getSkills() != null) {
            List<Long> listId = param.getSkills()
                    .stream().map(
                            skills -> skills.getId())
                    .collect(Collectors.toList());
            List<Skill> listJob = this.skillRepository.findByIdIn(listId);
            param.setSkills(listJob);

        }
        if (param.getCompany() != null) {
            Optional<Company> checkCompany = this.companyRepository.findById(param.getCompany().getId());
            if (checkCompany.isPresent()) {
                param.setCompany(checkCompany.get());
            } else {
                throw new IdInvalidException("Không có thông tin về công ty hoặc thông tin sai");
            }
        }
        Job newJob = this.jobRepository.save(param);
        ReponseJobCreatDTO res = mapperReponseJobCreatDTO_To_Job(newJob);

        return res;

    }

    public ReponseJobCreatDTO updateJobs(Job param) {
        Optional<Job> checkId = this.checkId(param.getId());
        Job update = checkId.get();
        update.setName(param.getName());
        update.setLocation(param.getLocation());
        update.setSalary(param.getSalary());
        update.setQuantity(param.getQuantity());
        update.setStartDate(param.getStartDate());
        update.setEndDate(param.getEndDate());
        update.setActive(param.isActive());
        if (param.getSkills() != null) {
            List<Long> listId = param.getSkills().stream().map(
                    skills -> skills.getId()).collect(Collectors.toList());

            List<Skill> listSkills = this.skillRepository.findByIdIn(listId);
            update.setSkills(listSkills);
        }
        this.jobRepository.save(update);

        ReponseJobCreatDTO res = mapperReponseJobCreatDTO_To_Job(update);
        return res;

    }

    public void deleteById(long id) {
        this.jobRepository.deleteById(id);
    }

    public ResultPaginationDTO filterJob(Specification<Job> spec,
            Pageable pageale) {
        Page<Job> pageJob = this.jobRepository.findAll(spec, pageale);
        ResultPaginationDTO res = new ResultPaginationDTO();
        Meta meta = new Meta();

        meta.setPage(pageale.getPageNumber() + 1);
        meta.setPageSize(pageale.getPageSize());

        meta.setPages(pageJob.getTotalPages());
        meta.setTotal(pageJob.getNumberOfElements());

        res.setMeta(meta);
        res.setResult(pageJob.getContent());
        return res;
    }

    // check----------------------------------------
    public Optional<Job> checkId(long id) {
        return this.jobRepository.findById(id);
    }

}
