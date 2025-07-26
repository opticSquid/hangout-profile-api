package com.hangout.core.profile_api.exceptions.handler;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.hangout.core.profile_api.exceptions.ConnectionFailedException;
import com.hangout.core.profile_api.exceptions.FileUploadFailedException;
import com.hangout.core.profile_api.exceptions.UnSupportedDateFormatException;
import com.hangout.core.profile_api.exceptions.UnSupportedFileTypeException;
import com.hangout.core.profile_api.exceptions.UnauthorizedAccessException;

@RestControllerAdvice
public class GlobalExceptionHadler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ConnectionFailedException.class)
    public ProblemDetail exceptionHandler(ConnectionFailedException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NO_CONTENT, ex.getMessage());
        problem.setTitle("Connection Failed...");
        return problem;
    }

    @ExceptionHandler(FileUploadFailedException.class)
    public ProblemDetail exceptionHandler(FileUploadFailedException ex) {
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

    @ExceptionHandler(UnSupportedDateFormatException.class)
    public ProblemDetail exceptionHandler(UnSupportedDateFormatException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problem.setTitle("Date Format not supported");
        return problem;
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail exceptionHandler(DataIntegrityViolationException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                "A profile already exists for the given user");
        problem.setTitle("Profile already exists");
        return problem;
    }
}
