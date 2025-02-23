package com.hangout.core.profile_api.exceptions.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.hangout.core.profile_api.exceptions.ConnectionFailed;
import com.hangout.core.profile_api.exceptions.FileUploadFailed;
import com.hangout.core.profile_api.exceptions.UnSupportedFileTypeException;
import com.hangout.core.profile_api.exceptions.UnauthorizedAccessException;

@RestControllerAdvice
public class GlobalExceptionHadler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ConnectionFailed.class)
    public ProblemDetail exceptionHandler(ConnectionFailed ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NO_CONTENT, ex.getMessage());
        problem.setTitle("Connection Failed...");
        return problem;
    }

    @ExceptionHandler(FileUploadFailed.class)
    public ProblemDetail exceptionHandler(FileUploadFailed ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        problem.setTitle("File Upload Failed due to technical Errors. Please try after some time");
        return problem;
    }

    @ExceptionHandler(UnauthorizedAccessException.class)
    public ProblemDetail exceptionHandler(UnauthorizedAccessException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, ex.getMessage());
        problem.setTitle("Unauthorized Access");
        return problem;
    }

    @ExceptionHandler(UnSupportedFileTypeException.class)
    public ProblemDetail exceptionHandler(UnSupportedFileTypeException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.UNSUPPORTED_MEDIA_TYPE, ex.getMessage());
        problem.setTitle("File type being uploaded is not supported");
        return problem;
    }
}
