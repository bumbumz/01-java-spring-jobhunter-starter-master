package vn.hoidanit.jobhunter.domain.Request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserInResLogin {
    private String email;
    private String name;
    private long id;

    

}
