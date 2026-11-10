package dev.decoder.rejectr.service;

import dev.decoder.rejectr.dto.CreateStoryRequest;
import dev.decoder.rejectr.entity.Story;
import dev.decoder.rejectr.repository.StoryRepository;
import dev.decoder.rejectr.security.DeviceTokenService;
import dev.decoder.rejectr.shred.Status;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StoryService {

    private final StoryRepository repository;
    private final DeviceTokenService deviceTokenService;

    public StoryService(StoryRepository repository, DeviceTokenService deviceTokenService) {
        this.repository = repository;
        this.deviceTokenService = deviceTokenService;
    }

    @Transactional
    public Story create(CreateStoryRequest request, String deviceToken) {
        Story story = new Story();
        story.setStage(request.stage());
        story.setOutcome(request.outcome());
        story.setBody(request.body());
        story.setStatus(Status.PUBLISHED);
        story.setDeviceHash(deviceTokenService.hash(deviceToken));
        return repository.save(story);
    }
}
