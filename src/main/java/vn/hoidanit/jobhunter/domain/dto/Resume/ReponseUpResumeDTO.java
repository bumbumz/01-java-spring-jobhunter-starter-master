package vn.hoidanit.jobhunter.domain.dto.Resume;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.hoidanit.jobhunter.util.constant.ResumeEnum;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReponseUpResumeDTO {
    private Instant updatedAt;

    private String updatedBy;
    
}
