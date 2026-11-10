package dev.decoder.rejectr;

import dev.decoder.rejectr.dto.CreateShredRequest;
import dev.decoder.rejectr.entity.Shred;
import dev.decoder.rejectr.exception.InvalidDeviceTokenException;
import dev.decoder.rejectr.repository.ShredRepository;
import dev.decoder.rejectr.security.DeviceTokenHasher;
import dev.decoder.rejectr.security.DeviceTokenService;
import dev.decoder.rejectr.service.ShredService;
import dev.decoder.rejectr.shred.Outcome;
import dev.decoder.rejectr.shred.Stage;
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
class ShredServiceTest {

    private static final String TOKEN = "device-token-1234567890";

    @Mock
    private ShredRepository repository;

    private final DeviceTokenHasher hasher = new DeviceTokenHasher("test-secret");
    private final DeviceTokenService deviceTokenService = new DeviceTokenService(hasher);
    private ShredService service;

    @BeforeEach
    void setUp() {
        service = new ShredService(repository, deviceTokenService);
    }

    @Test
    void storesHashedDeviceToken() {
        when(repository.save(any(Shred.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Shred shred = service.create(request(), TOKEN);

        assertNotNull(shred.getDeviceHash());
        assertEquals(hasher.hash(TOKEN), shred.getDeviceHash());
        assertNotEquals(TOKEN, shred.getDeviceHash());
    }

    @Test
    void storesNullWhenTokenMissing() {
        when(repository.save(any(Shred.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Shred shred = service.create(request(), null);

        assertNull(shred.getDeviceHash());
    }

    @Test
    void rejectsTooShortToken() {
        CreateShredRequest request = request();

        assertThrows(InvalidDeviceTokenException.class, () -> service.create(request, "too-short"));
    }

    @Test
    void rejectsTooLongToken() {
        CreateShredRequest request = request();
        String token = "a".repeat(129);

        assertThrows(InvalidDeviceTokenException.class, () -> service.create(request, token));
    }

    private static CreateShredRequest request() {
        return new CreateShredRequest(Stage.APPLIED_NEVER_HEARD_BACK, Outcome.GHOSTED, null);
    }
}
