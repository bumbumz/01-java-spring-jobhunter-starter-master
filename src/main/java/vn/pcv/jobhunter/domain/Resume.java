package vn.pcv.jobhunter.domain;

import java.time.Instant;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import vn.pcv.jobhunter.service.TokenService;
import vn.pcv.jobhunter.util.constant.LevelEnum;
import vn.pcv.jobhunter.util.constant.ResumeEnum;

@Entity
@Table(name = "resumes")
@Getter
@Setter
public class Resume {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NotBlank(message = "Không được để trống eami")
    private String email;
    @NotBlank(message = "Không được để trống url")

    private String url;

    @Enumerated(EnumType.STRING)
    private ResumeEnum status;
    private Instant createdAt;

    private String createdBy;
    private Instant updatedAt;

    private String updatedBy;

    @PrePersist
    public void beforeCreate() {
        this.createdBy = TokenService.getCurrentUserLogin().isPresent() == true
                ? TokenService.getCurrentUserLogin().get()
                : "";

        this.createdAt = Instant.now();
    }

    @PreUpdate
    public void beforeUpdate() {
        this.updatedBy = TokenService.getCurrentUserLogin().isPresent() == true
                ? TokenService.getCurrentUserLogin().get()
                : "";

        this.updatedAt = Instant.now();
    }

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "job_id")
    private Job job;

    // Job - resume : 1 - N

}
