package com.binh.todo_api.error;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalException {
//    MethodArgumentNotValidException là ngoại lệ được ném ra khi một đối số phương thức được chú thích với @Valid không vượt qua xác thực.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request){
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(err -> fieldErrors.put(err.getField(), err.getDefaultMessage()));
        fieldErrors.put("timestamp", Instant.now().toString());
        fieldErrors.put("status", "400");
        fieldErrors.put("mesage", "Validation failed");
        fieldErrors.put("requestId", requestId());

        ApiError err = new ApiError(Instant.now(), 400, ex.getMessage(), request.getRequestURI(), fieldErrors);

        return ResponseEntity.badRequest().body(err);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiError> handleNotFoundException(NotFoundException ex, HttpServletRequest request){
        ApiError err = new ApiError(Instant.now(), 404, ex.getMessage(), request.getRequestURI(), null);
        return ResponseEntity.status(404).body(err);
    }

    @ExceptionHandler(ConflicException.class)
    public ResponseEntity<ApiError> handleConflicException(ConflicException ex, HttpServletRequest request){
        ApiError err = new ApiError(Instant.now(), 409, ex.getMessage(), request.getRequestURI(), null);

        return ResponseEntity.status(409).body(err);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleUnknownException(Exception e){
        Map<String, String> response = new HashMap<>();
        response.put("timestamp", Instant.now().toString());
        response.put("status", "500");
        response.put("requestId", requestId());
        response.put("message", "Internal Server Error: " + e.getMessage());
        return ResponseEntity.status(500).body(response);
    }

    private String requestId() {
        String rid = MDC.get("requestId");
        return rid == null ? "" : rid;
    }

}
