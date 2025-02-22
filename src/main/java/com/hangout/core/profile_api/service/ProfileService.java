package com.hangout.core.profile_api.service;

import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.hangout.core.profile_api.exceptions.FileUploadFailed;
import com.hangout.core.profile_api.model.Media;
import com.hangout.core.profile_api.model.Profile;
import com.hangout.core.profile_api.repo.MediaRepo;
import com.hangout.core.profile_api.repo.ProfileRepo;
import com.hangout.core.profile_api.template.DefaultResponse;
import com.hangout.core.profile_api.template.FileUploadEvent;
import com.hangout.core.profile_api.template.Session;
import com.hangout.core.profile_api.util.AuthorizationService;
import com.hangout.core.profile_api.util.FileUploadService;
import com.hangout.core.profile_api.util.HashService;

import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final AuthorizationService authorizationService;
    private final FileUploadService fileUploadService;
    private final HashService hashService;
    private final ProfileRepo profileRepo;
    private final MediaRepo mediaRepo;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    @Value("${hangout.kafka.content.topic}")
    private String topic;

    @WithSpan(kind = SpanKind.INTERNAL)
    public DefaultResponse createProfile(String authorizationToken, String name,
            MultipartFile profilePicture) throws FileUploadException {
        Session session = authorizationService.authorizeUser(authorizationToken);
        Media media = new Media(hashService.computeInternalFilename(profilePicture), profilePicture.getContentType());
        media = mediaRepo.save(media);
        Profile profile = new Profile(session.userId(), name, media);
        fileUploadService.uploadFile(media.getFilename(), profilePicture);
        profile = profileRepo.save(profile);
        media.addPost(profile);
        mediaRepo.save(media);
        try {
            kafkaTemplate.send(topic, profilePicture.getContentType(),
                    new FileUploadEvent(media.getFilename(), session.userId()));
        } catch (IllegalStateException e) {
            throw new FileUploadFailed(
                    "Failed to produce kafka event for file: " + profilePicture.getOriginalFilename());
        }
        return new DefaultResponse("profile created");
    }
}
