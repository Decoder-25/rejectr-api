package dev.decoder.rejectr.service;

import dev.decoder.rejectr.dto.CreateShredRequest;
import dev.decoder.rejectr.entity.Shred;
import dev.decoder.rejectr.repository.ShredRepository;
import dev.decoder.rejectr.shred.Reason;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ShredService {

    private final ShredRepository repository;

    public ShredService(ShredRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Shred create(CreateShredRequest request) {
        Shred shred = new Shred();
        shred.setStage(request.stage());
        shred.setOutcome(request.outcome());
        shred.setReason(request.reason() != null ? request.reason() : Reason.NO_REASON_GIVEN);
        shred.setDeviceId(request.deviceId());
        return repository.save(shred);
    }
}
