package vn.hoidanit.jobhunter.config;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.hoidanit.jobhunter.domain.Permission;
import vn.hoidanit.jobhunter.domain.Role;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.service.TokenService;
import vn.hoidanit.jobhunter.service.UserService;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;
import vn.hoidanit.jobhunter.util.error.PermissionException;

public class PermissionInterceptor implements HandlerInterceptor {
    @Autowired
    UserService userService;

    @Override
    @Transactional
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response, Object handler)
            throws Exception {

        String path = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String requestURI = request.getRequestURI();
        String httpMethod = request.getMethod();
        System.out.println(">>> RUN preHandle");
        System.out.println(">>> path= " + path);
        System.out.println(">>> httpMethod= " + httpMethod);
        System.out.println(">>> requestURI= " + requestURI);

        String email = TokenService.getCurrentUserLogin().isPresent()
                ? TokenService.getCurrentUserLogin().get()
                : "";
        System.out.println(">>> email= " + email);
        if (email != null && !email.isEmpty()) {
            Optional<User> checkuser = this.userService.handleGetUserByUsername(email);
            if (checkuser.isPresent()) {
                Role role = checkuser.get().getRole();
                if (role != null) {
                    List<Permission> permission = role.getPermissions();
                    boolean isAllow = permission.stream().anyMatch(
                            item -> item.getApiPath().equals(path)
                                    && item.getMethod().equals(httpMethod)

                    );
                    if (isAllow == false) {
                        throw new PermissionException("Bạn không có quyền truy cập");
                    }
                    System.out.println(">>> isAllow= " + isAllow);

                } else {
                    throw new PermissionException("Bạn không có quyền truy cập");

                }
            }
        }
        return true;
    }
}
