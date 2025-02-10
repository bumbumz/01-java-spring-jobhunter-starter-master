package vn.pcv.jobhunter.domain.dto.Request;

import java.time.Instant;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.pcv.jobhunter.domain.Company;
import vn.pcv.jobhunter.domain.Skill;
import vn.pcv.jobhunter.util.constant.LevelEnum;

@Setter
@Getter

public class ReponseJobCreatDTO {
    private long id;

    private String name;
    private String location;

    private double salary;

    private int quantity;

    private LevelEnum level;

    private String description;

    private Instant startDate;

    private Instant endDate;

    private boolean active;

    private List<String> skills;

}
