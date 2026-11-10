package dev.decoder.rejectr.repository;

import dev.decoder.rejectr.entity.Story;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface StoryRepository extends JpaRepository<Story, UUID> {
}
