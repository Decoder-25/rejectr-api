package dev.decoder.rejectr.service;

import dev.decoder.rejectr.dto.CreateShredRequest;
import dev.decoder.rejectr.entity.Shred;
import dev.decoder.rejectr.repository.ShredRepository;
import dev.decoder.rejectr.security.DeviceTokenService;
import dev.decoder.rejectr.shred.Reason;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ShredService {

    private final ShredRepository repository;
    private final DeviceTokenService deviceTokenService;

    public ShredService(ShredRepository repository, DeviceTokenService deviceTokenService) {
        this.repository = repository;
        this.deviceTokenService = deviceTokenService;
    }

    @CacheEvict(cacheNames = "weeklyStats", allEntries = true)
    @Transactional
    public Shred create(CreateShredRequest request, String deviceToken) {
        Shred shred = new Shred();
        shred.setStage(request.stage());
        shred.setOutcome(request.outcome());
        shred.setReason(request.reason() != null ? request.reason() : Reason.NO_REASON_GIVEN);
        shred.setDeviceHash(deviceTokenService.hash(deviceToken));
        return repository.save(shred);
    }
}
