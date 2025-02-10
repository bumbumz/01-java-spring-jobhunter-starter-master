package vn.pcv.jobhunter.domain.Request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.pcv.jobhunter.domain.Role;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserInResLogin {
    private String email;
    private String name;
    private long id;
    private Role role;

}
