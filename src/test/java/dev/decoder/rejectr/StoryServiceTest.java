package dev.decoder.rejectr;

import dev.decoder.rejectr.dto.CreateStoryRequest;
import dev.decoder.rejectr.entity.Story;
import dev.decoder.rejectr.exception.InvalidDeviceTokenException;
import dev.decoder.rejectr.repository.StoryRepository;
import dev.decoder.rejectr.security.DeviceTokenHasher;
import dev.decoder.rejectr.security.DeviceTokenService;
import dev.decoder.rejectr.service.StoryService;
import dev.decoder.rejectr.shred.Outcome;
import dev.decoder.rejectr.shred.Stage;
import dev.decoder.rejectr.shred.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StoryServiceTest {

    private static final String TOKEN = "device-token-1234567890";
    private static final String BODY = "Five rounds over two months, then silence.";

    @Mock
    private StoryRepository repository;

    private final DeviceTokenHasher hasher = new DeviceTokenHasher("test-secret");
    private final DeviceTokenService deviceTokenService = new DeviceTokenService(hasher);
    private StoryService service;

    @BeforeEach
    void setUp() {
        service = new StoryService(repository, deviceTokenService);
    }

    @Test
    void storesPublishedStoryWithHashedDevice() {
        when(repository.save(any(Story.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Story story = service.create(request(), TOKEN);

        assertEquals(Stage.FINAL_ROUND, story.getStage());
        assertEquals(Outcome.GHOSTED, story.getOutcome());
        assertEquals(BODY, story.getBody());
        assertEquals(Status.PUBLISHED, story.getStatus());
        assertNotNull(story.getDeviceHash());
        assertEquals(hasher.hash(TOKEN), story.getDeviceHash());
        assertNotEquals(TOKEN, story.getDeviceHash());
    }

    @Test
    void storesNullDeviceWhenTokenMissing() {
        when(repository.save(any(Story.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Story story = service.create(request(), null);

        assertNull(story.getDeviceHash());
    }

    @Test
    void rejectsTooShortToken() {
        CreateStoryRequest request = request();

        assertThrows(InvalidDeviceTokenException.class, () -> service.create(request, "too-short"));
    }

    @Test
    void rejectsTooLongToken() {
        CreateStoryRequest request = request();
        String token = "a".repeat(129);

        assertThrows(InvalidDeviceTokenException.class, () -> service.create(request, token));
    }

    private static CreateStoryRequest request() {
        return new CreateStoryRequest(Stage.FINAL_ROUND, Outcome.GHOSTED, BODY);
    }
}
