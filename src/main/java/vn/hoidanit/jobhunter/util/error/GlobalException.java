package vn.hoidanit.jobhunter.util.error;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestCookieException;
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

    @ExceptionHandler(value = MissingRequestCookieException.class)
    public ResponseEntity<RestResponse<Object>> handleMissingRequestCookieException(MissingRequestCookieException e) {
        RestResponse<Object> response = new RestResponse<>();
        response.setStatusCode(HttpStatus.BAD_REQUEST.value());
        response.setError("lỗi liên quan đến cookie ");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

    }

    @ExceptionHandler(value = {
            UsernameNotFoundException.class,
            BadCredentialsException.class,

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
        response.setStatusCode(HttpStatus.NOT_FOUND.value());
        response.setError("Không tồn tại url này");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

    }

    @ExceptionHandler(value = NullPointerException.class)
    public ResponseEntity<RestResponse<Object>> handleNullPointerException(
            NullPointerException e) {
        RestResponse<Object> response = new RestResponse<>();
        response.setStatusCode(HttpStatus.NOT_FOUND.value());
        response.setError("lỗi cứu pháp");
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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RestResponse<Object>> validationError(MethodArgumentNotValidException ex) {
        BindingResult result = ex.getBindingResult();
        final List<FieldError> fieldErrors = result.getFieldErrors();

        RestResponse<Object> res = new RestResponse<Object>();
        res.setStatusCode(HttpStatus.BAD_REQUEST.value());
        res.setError(ex.getBody().getDetail());

        List<String> errors = fieldErrors.stream().map(f -> f.getDefaultMessage()).collect(Collectors.toList());
        res.setMessage(errors.size() > 1 ? errors : errors.get(0));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }

}
