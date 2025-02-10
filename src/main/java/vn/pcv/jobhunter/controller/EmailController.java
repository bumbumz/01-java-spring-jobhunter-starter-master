package vn.pcv.jobhunter.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.pcv.jobhunter.service.EmailService;
import vn.pcv.jobhunter.service.SubscriberService;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/v1")
public class EmailController {
    private final EmailService emailService;
    private final SubscriberService subscriberService;

    public EmailController(EmailService emailService, SubscriberService subscriberService) {
        this.emailService = emailService;
        this.subscriberService = subscriberService;
    }

    @GetMapping("/email")
    // @Scheduled(cron = "*/60 * * * * *")
    // @Transactional
    public String sendEmail() {
        // this.emailService.sendEmail();
        // this.emailService.sendEmailSync("tinguyen554@gmail.com",
        // "TestSendEmail",
        // "<h1><b>Halo<b/></h1>",
        // false, true);
        this.subscriberService.sendSubscribersEmailJobs();
        return "ok";
    }

}
