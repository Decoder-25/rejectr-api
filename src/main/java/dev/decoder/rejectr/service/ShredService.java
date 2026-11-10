package dev.decoder.rejectr.service;

import dev.decoder.rejectr.dto.CreateShredRequest;
import dev.decoder.rejectr.entity.Shred;
import dev.decoder.rejectr.exception.InvalidDeviceTokenException;
import dev.decoder.rejectr.repository.ShredRepository;
import dev.decoder.rejectr.security.DeviceTokenHasher;
import dev.decoder.rejectr.shred.Reason;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ShredService {

    private static final int MIN_TOKEN_LENGTH = 16;
    private static final int MAX_TOKEN_LENGTH = 128;

    private final ShredRepository repository;
    private final DeviceTokenHasher hasher;

    public ShredService(ShredRepository repository, DeviceTokenHasher hasher) {
        this.repository = repository;
        this.hasher = hasher;
    }

    @CacheEvict(cacheNames = "weeklyStats", allEntries = true)
    @Transactional
    public Shred create(CreateShredRequest request, String deviceToken) {
        Shred shred = new Shred();
        shred.setStage(request.stage());
        shred.setOutcome(request.outcome());
        shred.setReason(request.reason() != null ? request.reason() : Reason.NO_REASON_GIVEN);
        shred.setDeviceHash(hashDeviceToken(deviceToken));
        return repository.save(shred);
    }

    private String hashDeviceToken(String deviceToken) {
        if (deviceToken == null || deviceToken.isBlank()) {
            return null;
        }

        String token = deviceToken.trim();
        if (token.length() < MIN_TOKEN_LENGTH || token.length() > MAX_TOKEN_LENGTH) {
            throw new InvalidDeviceTokenException(
                    "device token must be between " + MIN_TOKEN_LENGTH + " and " + MAX_TOKEN_LENGTH + " characters");
        }

        return hasher.hash(token);
    }
}
