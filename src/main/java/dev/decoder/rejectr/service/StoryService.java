package dev.decoder.rejectr.service;

import dev.decoder.rejectr.dto.CreateStoryRequest;
import dev.decoder.rejectr.entity.Story;
import dev.decoder.rejectr.repository.StoryRepository;
import dev.decoder.rejectr.security.DeleteTokenService;
import dev.decoder.rejectr.security.DeviceTokenService;
import dev.decoder.rejectr.shred.Status;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StoryService {

    private final StoryRepository repository;
    private final DeviceTokenService deviceTokenService;
    private final DeleteTokenService deleteTokenService;

    public StoryService(StoryRepository repository,
                        DeviceTokenService deviceTokenService,
                        DeleteTokenService deleteTokenService) {
        this.repository = repository;
        this.deviceTokenService = deviceTokenService;
        this.deleteTokenService = deleteTokenService;
    }

    @Transactional
    public Story create(CreateStoryRequest request, String deviceToken) {
        Story story = new Story();
        story.setStage(request.stage());
        story.setOutcome(request.outcome());
        story.setBody(request.body());
        story.setStatus(Status.PUBLISHED);
        story.setDeviceHash(deviceTokenService.hash(deviceToken));

        String deleteToken = deleteTokenService.generate();
        story.setDeleteTokenHash(deleteTokenService.hash(deleteToken));

        return repository.save(story);
    }
}
