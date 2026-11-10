package dev.decoder.rejectr.security;

import dev.decoder.rejectr.exception.InvalidDeviceTokenException;
import org.springframework.stereotype.Service;

@Service
public class DeviceTokenService {

    private static final int MIN_TOKEN_LENGTH = 16;
    private static final int MAX_TOKEN_LENGTH = 128;

    private final DeviceTokenHasher hasher;

    public DeviceTokenService(DeviceTokenHasher hasher) {
        this.hasher = hasher;
    }

    public String hash(String deviceToken) {
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
