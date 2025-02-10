package vn.pcv.jobhunter.domain.dto.Resume;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.pcv.jobhunter.util.constant.ResumeEnum;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReponseGetByIdDTO {
    private long id;
    private String email;
    private String url;

    private ResumeEnum status;
    private String CompanyName;
    private Instant createdAt;

    private String createdBy;
    private Instant updatedAt;

    private String updatedBy;

    private UserInGetId user;
    private JobInGetById job;
}
