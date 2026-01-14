package com.example.projectiii.service;

import com.example.projectiii.dto.request.CommentRequest;
import com.example.projectiii.dto.request.search.SearchCommentRequest;
import com.example.projectiii.dto.response.comment.CommentResponse;
import com.example.projectiii.dto.response.comment.CommentShortResponse;
import com.example.projectiii.dto.response.PageResponse;
import com.example.projectiii.entity.Comment;
import com.example.projectiii.exception.UnauthorizedException;
import org.springframework.data.domain.Pageable;

/*
public interface CommentService {

    CommentShortResponse addComment(Integer postId, CommentRequest request);

    CommentShortResponse updateComment(Integer postId, Integer commentId, CommentRequest request) throws UnauthorizedException;

    PageResponse<CommentShortResponse> getComments(Pageable pageable);

    CommentShortResponse getComment(Integer id);

    void deleteComment(Integer id);

    //List<CommentResponse> getCommentByPost(Integer postId);

    PageResponse<CommentShortResponse> searchComment(Pageable pageable, SearchCommentRequest request);

    CommentResponse convertCommentToDTO(Comment comment);

    CommentShortResponse convertCommentToShortDTO(Comment comment);
}
*/
