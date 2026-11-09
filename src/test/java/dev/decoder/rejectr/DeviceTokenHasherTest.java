package dev.decoder.rejectr;

import dev.decoder.rejectr.security.DeviceTokenHasher;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DeviceTokenHasherTest {

    private final DeviceTokenHasher hasher = new DeviceTokenHasher("test-secret");

    @Test
    void hashesSameTokenDeterministically() {
        assertEquals(hasher.hash("device-token-1234567890"), hasher.hash("device-token-1234567890"));
    }

    @Test
    void hashesDifferentTokensDifferently() {
        assertNotEquals(hasher.hash("device-token-aaaaaaaaaa"), hasher.hash("device-token-bbbbbbbbbb"));
    }

    @Test
    void returnsNullForNullOrBlank() {
        assertNull(hasher.hash(null));
        assertNull(hasher.hash("   "));
    }

    @Test
    void producesSixtyFourLowercaseHexChars() {
        String hash = hasher.hash("device-token-1234567890");

        assertEquals(64, hash.length());
        assertTrue(hash.matches("[0-9a-f]{64}"));
    }

    @Test
    void outputDoesNotContainRawToken() {
        String token = "device-token-1234567890";

        assertNotEquals(token, hasher.hash(token));
    }
}
