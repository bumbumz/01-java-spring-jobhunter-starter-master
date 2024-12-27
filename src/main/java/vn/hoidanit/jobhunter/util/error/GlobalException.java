package vn.hoidanit.jobhunter.util.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import vn.hoidanit.jobhunter.domain.RestResponse;

@RestControllerAdvice
public class GlobalException {
    @ExceptionHandler(value = IdInvalidException.class)
    public ResponseEntity<RestResponse<Object>> handleIdInvalidException(IdInvalidException e) {
        RestResponse<Object> response = new RestResponse<>();
        response.setStatusCode(HttpStatus.BAD_REQUEST.value());
        response.setError(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

    }

    @ExceptionHandler(value = {
            UsernameNotFoundException.class,
            BadCredentialsException.class

    })
    public ResponseEntity<RestResponse<Object>> handleExceptionToken(Exception e) {
        RestResponse<Object> response = new RestResponse<>();
        response.setStatusCode(HttpStatus.BAD_REQUEST.value());
        response.setError(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

    }

    @ExceptionHandler(value = NoResourceFoundException.class)
    public ResponseEntity<RestResponse<Object>> handleNoResourceFoundException(
            NoResourceFoundException e) {
        RestResponse<Object> response = new RestResponse<>();
        response.setStatusCode(HttpStatus.BAD_REQUEST.value());
        response.setError("Không tồn tại url này");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

    }

    @ExceptionHandler(value = MethodArgumentTypeMismatchException.class)
    public ResponseEntity<RestResponse<Object>> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException e) {
        RestResponse<Object> response = new RestResponse<>();
        response.setStatusCode(HttpStatus.BAD_REQUEST.value());
        response.setError("Lỗi giá trị truyền vào ở url");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

    }

}
