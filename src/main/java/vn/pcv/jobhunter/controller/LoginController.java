package vn.pcv.jobhunter.controller;

import java.util.Optional;

import org.apache.catalina.security.SecurityUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import vn.pcv.jobhunter.domain.User;
import vn.pcv.jobhunter.domain.Request.ResLoginDTO;
import vn.pcv.jobhunter.domain.Request.ResUserDTO;
import vn.pcv.jobhunter.domain.Request.UserGetAccount;
import vn.pcv.jobhunter.domain.Request.UserInResLogin;
import vn.pcv.jobhunter.domain.dto.Request.RequestLoginDTO;
import vn.pcv.jobhunter.repository.UserRepository;
import vn.pcv.jobhunter.service.TokenService;
import vn.pcv.jobhunter.service.UserService;
import vn.pcv.jobhunter.util.annotation.AppMessage;
import vn.pcv.jobhunter.util.error.IdInvalidException;

import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/v1")
public class LoginController {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final TokenService tokenService;
    private final UserService userService;
    @Value("${pcv.jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenExpiration;

    public LoginController(
            AuthenticationManagerBuilder authenticationManagerBuilder,
            TokenService tokenService,
            UserService userService) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.tokenService = tokenService;
        this.userService = userService;

    }

    @PostMapping("/auth/login")
    public ResponseEntity<ResLoginDTO> login(@Valid @RequestBody RequestLoginDTO loginlogin) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginlogin.getUsername(), loginlogin.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // lưu trữ thông tin người đăng nhập
        SecurityContextHolder.getContext().setAuthentication(authentication);

        Optional<User> currenUser = this.userService.handleGetUserByUsername(loginlogin.getUsername());
        User userInRes = currenUser.get();

        ResLoginDTO res = new ResLoginDTO();

        UserInResLogin user = new UserInResLogin(
                userInRes.getEmail(),
                userInRes.getName(),
                userInRes.getId(),
                userInRes.getRole());
        res.setUser(user);

        
        String access_token = this.tokenService.createAccessToken(authentication.getName(), res);
        res.setAccessToken(access_token);
        // tạo refesh token
        String refreshToken = this.tokenService.createRefreshToken(loginlogin.getUsername(), res);
        ResponseCookie cookie = ResponseCookie
                .from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpiration)
                // .domain("example.com")
                .build();
        this.userService.updateTokenUser(refreshToken, loginlogin.getUsername());
        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(res);
    }

    @GetMapping("/auth/account")
    @AppMessage("Lấy dữ liệu thành công")
    public ResponseEntity<UserGetAccount> getAccount() {
        String email = TokenService.getCurrentUserLogin().isPresent() ? TokenService.getCurrentUserLogin().get() : "";
        Optional<User> currenUser = this.userService.handleGetUserByUsername(email);
        UserInResLogin user = new UserInResLogin();
        if (currenUser.isPresent()) {

            user.setEmail(currenUser.get().getEmail());
            user.setName(currenUser.get().getName());
            user.setId(currenUser.get().getId());
            user.setRole(currenUser.get().getRole());

        }
        UserGetAccount res = new UserGetAccount();
        res.setUser(user);
        return ResponseEntity.ok().body(res);
    }

    @GetMapping("/auth/refresh")
    public ResponseEntity<ResLoginDTO> getRefreshToken(
            @CookieValue("refreshToken") String refresh_token) throws IdInvalidException {
        Jwt jwt = this.tokenService.checkValidRefeshToken(refresh_token);
        String email = jwt.getSubject();
        User isuser = this.userService.getUserbyRefreshTokenAndEmail(refresh_token, email);
        if (isuser == null) {
            throw new IdInvalidException("refresh token khong hop le");
        }
        // tạo token mới

        ResLoginDTO res = new ResLoginDTO();

        UserInResLogin user = new UserInResLogin(isuser.getEmail(), isuser.getName(), isuser.getId(),
                isuser.getRole() != null ? isuser.getRole() : null);
        res.setUser(user);
        String access_token = this.tokenService.createAccessToken(email, res);
        res.setAccessToken(access_token);
        // tạo refesh token
        String new_refreshToken = this.tokenService.createRefreshToken(email, res);
        ResponseCookie cookie = ResponseCookie
                .from("refreshToken", new_refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpiration)
                // .domain("example.com")
                .build();
        this.userService.updateTokenUser(new_refreshToken, email);
        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(res);
    }

    @GetMapping("/auth/logout")
    public ResponseEntity<Void> logOut() throws IdInvalidException {
        String email = TokenService.getCurrentUserLogin().isPresent() ? TokenService.getCurrentUserLogin().get() : "";

        if (email.equals("")) {
            throw new IdInvalidException("Token khong ton tai");
        }
        this.userService.updateTokenUser(null, email);

        ResponseCookie cookie = ResponseCookie
                .from("refreshToken", null)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                // .domain("example.com")
                .build();
        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(null);

    }
     @PostMapping("/auth/register")
     public  ResponseEntity<ResUserDTO> registerUser(@Valid @RequestBody User userParam) throws IdInvalidException
     {
        ResUserDTO user = userService.createUser(userParam);

        return ResponseEntity.status(HttpStatus.CREATED).body(user);
     }

}
