package com.gridapp.app.repository;

import com.gridapp.app.entity.Bot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BotRepo extends JpaRepository<Bot, String> {
}
