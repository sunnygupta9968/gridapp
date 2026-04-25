package com.gridapp.app.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;


@Entity
@Data
@NoArgsConstructor
@Table(name="bots")
public class Bot {
    @Id
    @UuidGenerator
    private String id;
    @Column(nullable = false)
    private String name;
    @Column(name="persona_description", columnDefinition = "TEXT")
    private String personaDescription;


}
