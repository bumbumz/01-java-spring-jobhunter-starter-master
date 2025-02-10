package vn.pcv.jobhunter.domain.dto.file;

import java.time.Instant;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter

public class ReponseUploadFile {
    private String fileName;
    private Instant uploadedAt;

}
