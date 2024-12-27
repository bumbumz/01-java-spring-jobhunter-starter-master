package vn.hoidanit.jobhunter.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.dto.LoginDTO;
import vn.hoidanit.jobhunter.domain.dto.ResLoginDTO;
import vn.hoidanit.jobhunter.service.TokenService;

@RestController
public class LoginController {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final TokenService tokenService;

    public LoginController(
            AuthenticationManagerBuilder authenticationManagerBuilder,
            TokenService tokenService) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.tokenService = tokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<ResLoginDTO> login(@Valid @RequestBody LoginDTO loginlogin) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginlogin.getUsername(), loginlogin.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        String access_token = this.tokenService.createToken(authentication);

        //lưu trữ thông tin người đăng nhập 
        SecurityContextHolder.getContext().setAuthentication(authentication);

        ResLoginDTO res = new ResLoginDTO();
        res.setToken(access_token);
        return ResponseEntity.ok().body(res);
    }

}
