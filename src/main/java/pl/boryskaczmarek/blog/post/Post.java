package pl.boryskaczmarek.blog.post;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;

import java.time.LocalDateTime;

public record Post(
        @Id Integer id,
        @NotEmpty String title,
        @JsonProperty("author_id") Integer authorId,
        @NotEmpty String content,
        @JsonProperty("created_at") LocalDateTime createdAt,
        @JsonProperty("updated_at") LocalDateTime updatedAt,
        @Version Integer version
) {
    public Post {
        if (createdAt.isAfter(updatedAt)) {
            throw new IllegalArgumentException("createdAt must NOT be after updatedAt");
        }
    }
}
