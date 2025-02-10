package vn.pcv.jobhunter.domain.Request;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class ResLoginDTO {
    @JsonProperty("access_token")
    private String accessToken;
    private UserInResLogin user;

    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserInsideToken {

        private String email;
        private String name;
        private long id;

    }

}
