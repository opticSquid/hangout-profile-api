package com.hangout.core.profile_api.controller;

import java.util.Optional;

import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.hangout.core.profile_api.model.Profile;
import com.hangout.core.profile_api.service.ProfileService;
import com.hangout.core.profile_api.template.DefaultResponse;

import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/profile")
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileService profileService;

    @WithSpan(kind = SpanKind.SERVER)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public DefaultResponse createProfile(
            @RequestHeader(name = "Authorization") String authorizationToken,
            @RequestPart(name = "name") String name,
            @RequestPart(name = "profile-picture") MultipartFile profilePicture) throws FileUploadException {
        return profileService.createProfile(authorizationToken, name, profilePicture);
    }

    @WithSpan(kind = SpanKind.SERVER)
    @GetMapping
    public ResponseEntity<Profile> getProfile(@RequestHeader(name = "Authorization") String authorizationToken) {
        Optional<Profile> profileOpt = profileService.getProfile(authorizationToken);
        if (profileOpt.isPresent()) {
            return new ResponseEntity<>(profileOpt.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
