package com.hangout.core.profile_api.controller;

import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.hangout.core.profile_api.service.ProfileService;
import com.hangout.core.profile_api.template.DefaultResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/profile")
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileService profileService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public DefaultResponse createProfile(
            @RequestHeader(name = "Authorization") String authorizationToken,
            @RequestPart(name = "name") String name,
            @RequestPart(name = "profile-picture") MultipartFile profilePicture) throws FileUploadException {
        return profileService.createProfile(authorizationToken, name, profilePicture);
    }

}
