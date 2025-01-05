package vn.hoidanit.jobhunter.domain.dto.Request;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.hoidanit.jobhunter.util.constant.GenderEnum;

@Getter
@Setter
@JsonInclude(Include.NON_NULL)
public class RequestUserUpdate {
    private long id;

    @NotBlank(message = "Không được để trống name")
    private String name;

    private int age;

    @Enumerated(EnumType.STRING)
    private GenderEnum gender;

    @NotBlank(message = "Không được để trống address")
    private String address;
    private Instant createdAt;
    private Instant updatedAt;
    private String updatedBy;

}
