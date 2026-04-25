package com.gridapp.app.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@Table(name="posts")
public class Post {
    @Id
    @UuidGenerator
    @Column(name = "post_id")
    private String id;

    @Column(name = "author_id", nullable = false)
    private String authorId;

    @Enumerated(EnumType.STRING)
    @Column(name = "author_type", nullable = false)
    private AuthorType authorType;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Comment> comments = new ArrayList<>();



    public enum AuthorType {
        USER,
        BOT
    }
}
