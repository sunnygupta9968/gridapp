package com.gridapp.app.entity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Data
@NoArgsConstructor
@Table(name="users")
public class User {
    @Id
    @UuidGenerator
    private String id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(name = "is_premium")
    private boolean isPremium;
}
