package vn.pcv.jobhunter.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import vn.pcv.jobhunter.domain.Job;
import vn.pcv.jobhunter.domain.Skill;
import vn.pcv.jobhunter.domain.Subscriber;
import vn.pcv.jobhunter.domain.Request.ResEmailJob;
import vn.pcv.jobhunter.repository.JobRepository;
import vn.pcv.jobhunter.repository.SkillRepository;
import vn.pcv.jobhunter.repository.SubscriberRepository;

@Service
public class SubscriberService {
    private final SubscriberRepository subscriberRepository;
    private final SkillRepository skillRepository;
    private final JobRepository jobRepository;
    private final EmailService emailService;

    public SubscriberService(SubscriberRepository subscriberRepository,
            SkillRepository skillRepository,
            JobRepository jobRepository,
            EmailService emailService) {
        this.subscriberRepository = subscriberRepository;
        this.skillRepository = skillRepository;
        this.jobRepository = jobRepository;
        this.emailService = emailService;
    }

    // ======================================================================================

    public void cron() {
        System.out.println("TEST CRON");
    }

    public Subscriber createSub(Subscriber param) {

        List<Long> idList = param.getSkills().stream().map(
                item -> item.getId()).collect(Collectors.toList());
        List<Skill> skill = this.skillRepository.findByIdIn(idList);
        param.setSkills(skill);
        return this.subscriberRepository.save(param);
    }

    public Subscriber updateSub(Subscriber param) {
        Subscriber res = this.checkId(param.getId()).get();
        if (param.getSkills() != null) {
            List<Long> idList = param.getSkills().stream().map(
                    item -> item.getId()).collect(Collectors.toList());
            List<Skill> skill = this.skillRepository.findByIdIn(idList);
            if (skill.size() != 0) {
                res.setSkills(skill);

            }
        }
        return this.subscriberRepository.save(res);

    }

    public ResEmailJob convertJobToSendEmail(Job job) {
        ResEmailJob res = new ResEmailJob();
        res.setName(job.getName());
        res.setSalary(job.getSalary());
        res.setCompany(new ResEmailJob.CompanyEmail(job.getCompany().getName()));
        List<Skill> skills = job.getSkills();
        List<ResEmailJob.SkillEmail> skillEmails = skills.stream()
                .map(
                        item -> new ResEmailJob.SkillEmail(item.getName()))
                .collect(Collectors.toList());
        res.setSkills(skillEmails);
        return res;
    }

    public void sendSubscribersEmailJobs() {
        List<Subscriber> listSubs = this.subscriberRepository.findAll();
        if (listSubs != null && listSubs.size() > 0) {
            for (Subscriber sub : listSubs) {
                List<Skill> listSkills = sub.getSkills();
                if (listSkills != null && listSkills.size() > 0) {
                    List<Job> listJobs = this.jobRepository.findBySkillsIn(listSkills);
                    if (listJobs != null && listJobs.size() > 0) {

                        List<ResEmailJob> arr = listJobs.stream().map(
                                job -> this.convertJobToSendEmail(job)).collect(Collectors.toList());

                        this.emailService.sendEmailFromTemplateSync(
                                sub.getEmail(),
                                "Cơ hội việc làm hot đang chờ đón bạn, khám phá ngay",
                                "job",
                                sub.getName(),
                                arr);
                    }
                }
            }
        }
    }

    // =====================================================================================
    // Check data
    public Optional<Subscriber> checkId(long id) {
        return this.subscriberRepository.findById(id);
    }

    public Optional<Subscriber> checkEmail(String email) {
        return this.subscriberRepository.findByEmail(email);
    }

}
