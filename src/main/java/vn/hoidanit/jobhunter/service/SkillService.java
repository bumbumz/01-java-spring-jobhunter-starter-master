package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Skill;
import vn.hoidanit.jobhunter.domain.Request.Meta;
import vn.hoidanit.jobhunter.domain.Request.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.SkillRepository;

@Service
public class SkillService {
    private final SkillRepository skillRepository;

    public SkillService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    public Skill creatSkills(Skill param) {

        return this.skillRepository.save(param);
    }

    public Skill updateSkillsById(Skill param) {
        return this.skillRepository.save(param);
    }

    public ResultPaginationDTO filterSkill(Specification<Skill> spec,
            Pageable page) {
        Page<Skill> skillpag = this.skillRepository.findAll(spec, page);
        ResultPaginationDTO res = new ResultPaginationDTO();
        Meta meta = new Meta();
        meta.setPage(page.getPageNumber() + 1);
        meta.setPageSize(page.getPageSize());
        meta.setPages(skillpag.getTotalPages());
        meta.setTotal(skillpag.getTotalElements());

        res.setMeta(meta);
        res.setResult(skillpag.getContent());
        return res;

    }

    public void delete(Skill skill) {
        // xóa job liên quan trong bảng ảo
        // từ entity skill gọi đến list jobs
        // job là bảng cha nên sẽ từ job xóa đc job đang nối skill muốn xóa
        skill.getJobs().forEach(job -> job.getSkills().remove(skill));
        skill.getSubscribers().forEach(job -> job.getSkills().remove(skill));
        this.skillRepository.delete(skill);

    }

    // check Skill
    // ---------------------------------------------------------------------------
    public Optional<Skill> checkName(String name) {
        return this.skillRepository.findByName(name);
    }

    public Optional<Skill> checkId(Long id) {
        return this.skillRepository.findById(id);
    }

}
