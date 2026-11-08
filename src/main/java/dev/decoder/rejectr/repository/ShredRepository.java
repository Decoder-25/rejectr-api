package dev.decoder.rejectr.repository;

import dev.decoder.rejectr.entity.Shred;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ShredRepository extends JpaRepository<Shred, UUID> {
}
