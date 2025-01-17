package vn.hoidanit.jobhunter.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.Subscriber;
import vn.hoidanit.jobhunter.repository.SkillRepository;
import vn.hoidanit.jobhunter.service.SubscriberService;
import vn.hoidanit.jobhunter.service.TokenService;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/v1")
public class SubscriberController {

    private final SubscriberService subscriberService;

    public SubscriberController(SubscriberService subscriberService) {
        this.subscriberService = subscriberService;

    }

    @PostMapping("/subscribers")
    public ResponseEntity<Subscriber> createSubcribers(@Valid @RequestBody Subscriber param) throws IdInvalidException {
        Optional<Subscriber> checkEmail = this.subscriberService.checkEmail(param.getEmail());
        if (checkEmail.isPresent()) {
            throw new IdInvalidException("Bạn đã đăng ký kỷ năng");
        }
        Subscriber res = this.subscriberService.createSub(param);
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @PutMapping("/subscribers")
    public ResponseEntity<Subscriber> updateSub(@RequestBody Subscriber param) throws IdInvalidException {

        Optional<Subscriber> checkId = this.subscriberService.checkId(param.getId());
        if (!checkId.isPresent()) {
            throw new IdInvalidException("Bạn chưachưa đăng ký kỷ năng");
        }
        Subscriber res = this.subscriberService.updateSub(param);
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @PostMapping("/subscribers/skills")
    public ResponseEntity<Subscriber> getSubSkill() throws IdInvalidException {
        String email = TokenService.getCurrentUserLogin().isPresent()
                ? TokenService.getCurrentUserLogin().get()
                : "";
        Optional<Subscriber> checkEmail = this.subscriberService.checkEmail(email);
        if (!checkEmail.isPresent()) {
            throw new IdInvalidException("Khong tim thay nguoi dung");
        }
        return ResponseEntity.ok().body(checkEmail.get());
    }

}
