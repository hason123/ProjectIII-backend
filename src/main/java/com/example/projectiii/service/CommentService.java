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

    CommentShortResponse addComment(Long postId, CommentRequest request);

    CommentShortResponse updateComment(Long postId, Long commentId, CommentRequest request) throws UnauthorizedException;

    PageResponse<CommentShortResponse> getComments(Pageable pageable);

    CommentShortResponse getComment(Long id);

    void deleteComment(Long id);

    //List<CommentResponse> getCommentByPost(Long postId);

    PageResponse<CommentShortResponse> searchComment(Pageable pageable, SearchCommentRequest request);

    CommentResponse convertCommentToDTO(Comment comment);

    CommentShortResponse convertCommentToShortDTO(Comment comment);
}
*/
