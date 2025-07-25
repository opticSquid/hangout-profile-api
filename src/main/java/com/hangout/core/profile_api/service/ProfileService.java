package com.hangout.core.profile_api.service;

import java.math.BigInteger;
import java.util.Optional;

import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.hangout.core.profile_api.exceptions.UnSupportedFileTypeException;
import com.hangout.core.profile_api.model.Media;
import com.hangout.core.profile_api.model.Profile;
import com.hangout.core.profile_api.repo.MediaRepo;
import com.hangout.core.profile_api.repo.ProfileRepo;
import com.hangout.core.profile_api.template.DefaultResponse;
import com.hangout.core.profile_api.template.Session;
import com.hangout.core.profile_api.util.AuthorizationService;
import com.hangout.core.profile_api.util.FileUploadService;
import com.hangout.core.profile_api.util.HashService;

import io.opentelemetry.instrumentation.annotations.WithSpan;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final AuthorizationService authorizationService;
    private final FileUploadService fileUploadService;
    private final HashService hashService;
    private final ProfileRepo profileRepo;
    private final MediaRepo mediaRepo;

    @WithSpan
    @Transactional
    public DefaultResponse createProfile(String authorizationToken, String name,
            MultipartFile profilePicture) throws FileUploadException {
        if (!profilePicture.getContentType().startsWith("image/")) {
            throw new UnSupportedFileTypeException(
                    "The content type " + profilePicture.getContentType() + " is not supported");
        }
        Session session = authorizationService.authorizeUser(authorizationToken);
        String filename = hashService.computeInternalFilename(profilePicture);
        Optional<Media> mediaOpt = mediaRepo.findById(filename);
        if (mediaOpt.isPresent()) {
            Media media = mediaOpt.get();
            Profile profile = new Profile(session.userId(), name, media);
            profile = profileRepo.save(profile);
            media.addPost(profile);
            mediaRepo.save(media);
        } else {
            Media media = new Media(filename, profilePicture.getContentType());
            media = mediaRepo.save(media);
            Profile profile = new Profile(session.userId(), name, media);
            fileUploadService.uploadFile(filename, profilePicture);
            profile = profileRepo.save(profile);
            media.addPost(profile);
            mediaRepo.save(media);
        }

        return new DefaultResponse("profile created");
    }

    @WithSpan
    public Optional<Profile> getProfile(String authorizationToken) {
        Session session = authorizationService.authorizeUser(authorizationToken);
        return profileRepo.findByUserId(session.userId());
    }

    @WithSpan
    public Optional<Profile> getProfile(BigInteger userId) {
        return profileRepo.findByUserId(userId);
    }
}
