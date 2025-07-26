package com.hangout.core.profile_api.service;

import java.math.BigInteger;
import java.time.ZonedDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.hangout.core.profile_api.exceptions.UnSupportedFileTypeException;
import com.hangout.core.profile_api.model.Gender;
import com.hangout.core.profile_api.model.Media;
import com.hangout.core.profile_api.model.Profile;
import com.hangout.core.profile_api.repo.MediaRepo;
import com.hangout.core.profile_api.repo.ProfileRepo;
import com.hangout.core.profile_api.template.DefaultResponse;
import com.hangout.core.profile_api.template.PublicProfileProjection;
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

    @WithSpan(value = "create profile service")
    @Transactional
    public DefaultResponse createProfile(String authorizationToken, String name, Gender gender, ZonedDateTime dob,
            MultipartFile profilePicture) {
        if (!profilePicture.getContentType().startsWith("image/")) {
            throw new UnSupportedFileTypeException(
                    "The content type " + profilePicture.getContentType() + " is not supported");
        }
        Session session = authorizationService.authorizeUser(authorizationToken);
        String filename = hashService.computeInternalFilename(profilePicture);
        Optional<Media> mediaOpt = mediaRepo.findById(filename);
        Media media;
        if (mediaOpt.isPresent()) {
            media = mediaOpt.get();
        } else {
            Media m = new Media(filename, profilePicture.getContentType());
            fileUploadService.uploadFile(filename, profilePicture);
            media = mediaRepo.save(m);
        }
        Profile profile = new Profile(session.userId(), name, gender, dob, media);
        profile = profileRepo.save(profile);
        media.addPost(profile);
        mediaRepo.save(media);
        return new DefaultResponse("profile created");
    }

    @WithSpan(value = "get own profile service")
    public Optional<Profile> getOwnProfile(String authorizationToken) {
        Session session = authorizationService.authorizeUser(authorizationToken);
        return profileRepo.findByUserId(session.userId());
    }

    @WithSpan(value = "get public profile service")
    public Optional<PublicProfileProjection> getPublicProile(BigInteger userId) {
        return profileRepo.findPublicProfileDetails(userId);
    }
}
