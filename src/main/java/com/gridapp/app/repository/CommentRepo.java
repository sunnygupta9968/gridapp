package com.gridapp.app.repository;
import com.gridapp.app.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepo extends JpaRepository<Comment, String> {
    List<Comment> findByPost_Id(String postId);

}
