package com.gridapp.app.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@Table(name="comments")
public class Comment {
    @Id
    @UuidGenerator
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    @JsonIgnore
    private Post post;

    @Column(name = "author_id", nullable = false)
    private String authorId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
    @Column(name = "depth_level", nullable = false)
    private long depthLevel;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public enum AuthorType {
        USER,
        BOT
    }
}
