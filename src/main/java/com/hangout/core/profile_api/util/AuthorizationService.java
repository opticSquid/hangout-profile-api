package com.hangout.core.profile_api.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.hangout.core.profile_api.exceptions.UnauthorizedAccessException;
import com.hangout.core.profile_api.template.Session;
import com.hangout.core.profile_api.template.UserValidationRequest;

import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthorizationService {
    private final RestClient restClient;
    @Value("${hangout.auth-service.url}")
    private String authServiceURL;

    @WithSpan(kind = SpanKind.CLIENT)
    public Session authorizeUser(String authHeader) {
        ResponseEntity<Session> response = restClient
                .post()
                .uri(authServiceURL + "/auth-api/v1/internal/validate")
                .body(new UserValidationRequest(authHeader))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .toEntity(Session.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        } else {
            throw new UnauthorizedAccessException(
                    "User is not valid or user does not have permission to perform current action");
        }
    }
}
