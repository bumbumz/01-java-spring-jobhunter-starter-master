package vn.hoidanit.jobhunter.domain.dto.Resume;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class ReponseResmeDTO {
    private long id;
    private Instant createdAt;
    private Instant updatedAt;

    private String createdBy;

    private String updatedBy;
}
