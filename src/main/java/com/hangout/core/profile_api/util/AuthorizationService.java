package com.hangout.core.profile_api.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import com.hangout.core.profile_api.exceptions.UnauthorizedAccessException;
import com.hangout.core.profile_api.template.Session;
import com.hangout.core.profile_api.template.UserValidationRequest;

import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthorizationService {
    private final RestClient restClient;
    @Value("${hangout.auth-service.url}")
    private String authServiceURL;

    @WithSpan(kind = SpanKind.CLIENT)
    public Session authorizeUser(String authHeader) {
        try {
            ResponseEntity<Session> response = restClient
                    .post()
                    .uri(authServiceURL + "/auth-api/v1/internal/validate")
                    .body(new UserValidationRequest(authHeader))
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .toEntity(Session.class);
            return response.getBody();
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatusCode.valueOf(401)) {
                ProblemDetail problemDetail = null;
                try {
                    problemDetail = exception.getResponseBodyAs(ProblemDetail.class);
                } catch (Exception e) {
                    log.warn("Could not parse error response as ProblemDetail: {}", e.getMessage());
                }
                String errorMessage = "User token has expired";
                if (problemDetail != null && problemDetail.getDetail() != null) {
                    errorMessage = problemDetail.getDetail();
                } else if (exception.getMessage() != null) {
                    errorMessage = exception.getMessage();
                }

                log.debug("Unauthorized access: {}", errorMessage);
                throw new UnauthorizedAccessException(errorMessage);
            } else {
                throw exception;
            }

        }
    }
}
