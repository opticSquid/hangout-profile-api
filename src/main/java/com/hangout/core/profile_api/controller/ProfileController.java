package com.hangout.core.profile_api.controller;

import java.math.BigInteger;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.Optional;

import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.hangout.core.profile_api.exceptions.UnSupportedDateFormatException;
import com.hangout.core.profile_api.model.Gender;
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

    @WithSpan(kind = SpanKind.SERVER, value = "create profile controller")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public DefaultResponse createProfile(
            @RequestHeader(name = "Authorization") String authorizationToken,
            @RequestPart(name = "name") String name,
            @RequestPart(name = "gender") String g,
            @RequestPart(name = "dob") String dob,
            @RequestPart(name = "profile-picture") MultipartFile profilePicture) throws FileUploadException {
        Gender gender;
        if (g == Gender.FEMALE.label) {
            gender = Gender.FEMALE;
        } else if (g == Gender.MALE.label) {
            gender = Gender.MALE;
        } else {
            gender = Gender.OTHER;
        }
        try {
            ZonedDateTime dateOfBirth = ZonedDateTime.parse(dob);
            return profileService.createProfile(authorizationToken, name, gender, dateOfBirth, profilePicture);
        } catch (DateTimeParseException ex) {
            throw new UnSupportedDateFormatException(
                    "Unsupported date format provided for Date of Birth field. Provide this lind of date format: 2007-12-03T10:15:30+01:00");
        }
    }

    @WithSpan(kind = SpanKind.SERVER, value = "get own profile")
    @GetMapping
    public ResponseEntity<Profile> getProfile(@RequestHeader(name = "Authorization") String authorizationToken) {
        Optional<Profile> profileOpt = profileService.getProfile(authorizationToken);
        if (profileOpt.isPresent()) {
            return new ResponseEntity<>(profileOpt.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @WithSpan(kind = SpanKind.SERVER, value = "get other's profiles")
    @GetMapping("/{userId}")
    public ResponseEntity<Profile> getProfile(@PathVariable BigInteger userId) {
        Optional<Profile> profileOpt = profileService.getProfile(userId);
        if (profileOpt.isPresent()) {
            return new ResponseEntity<>(profileOpt.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
