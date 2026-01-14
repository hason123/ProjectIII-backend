package com.example.projectiii.repository;

import com.example.projectiii.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer>, JpaSpecificationExecutor<Comment> {
    List<Comment> findAllByBook_BookId(Integer bookId);

    List<Comment> findAllByUser_Id(Integer id);

    List<Comment> findAllByParent_CommentId(Integer parentCommentId);







}
