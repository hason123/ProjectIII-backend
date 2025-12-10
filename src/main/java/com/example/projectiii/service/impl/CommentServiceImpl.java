/*
package com.example.projectiii.service.impl;

import com.example.projectiii.config.MessageConfig;
import com.example.projectiii.constant.MessageError;
import com.example.projectiii.constant.RoleType;
import com.example.projectiii.dto.request.CommentRequest;
import com.example.projectiii.dto.request.search.SearchCommentRequest;
import com.example.projectiii.dto.response.comment.CommentResponse;
import com.example.projectiii.dto.response.comment.CommentShortResponse;
import com.example.projectiii.dto.response.PageResponse;
import com.example.projectiii.entity.Comment;
//import com.example.projectiii.entity.Post;
import com.example.projectiii.entity.User;
import com.example.projectiii.exception.ResourceNotFoundException;
import com.example.projectiii.exception.UnauthorizedException;
import com.example.projectiii.repository.CommentRepository;
//import com.example.projectiii.repository.PostRepository;
import com.example.projectiii.repository.UserRepository;
import com.example.projectiii.service.CommentService;
import com.example.projectiii.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
@Slf4j
@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final MessageConfig messageConfig;
    private final UserService userService;

    public CommentServiceImpl(CommentRepository commentRepository, UserRepository userRepository, MessageConfig messageConfig, @Lazy UserService userService) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.messageConfig = messageConfig;
        this.userService = userService;
    }

    @Override
    public CommentShortResponse addComment(Long postId, CommentRequest request) {
        log.info("Add comment in post with id: {}", postId);
        Post post = postRepository.findById(postId).orElseThrow(() -> new ResourceNotFoundException(MessageError.POST_NOT_FOUND));
        Comment comment = new Comment();
        comment.setCommentDetail(request.getContent());
        comment.setUser(userService.getCurrentUser());
        comment.setPost(post);
        if (request.getParentCommentId() != null) {
            Comment parentComment = commentRepository.findById(request.getParentCommentId())
                    .orElseThrow(() -> new ResourceNotFoundException(messageConfig.getMessage(MessageError.COMMENT_NOT_FOUND, request.getParentCommentId())));
            comment.setParent(parentComment);
        } else comment.setParent(null);
        commentRepository.save(comment);
        return convertCommentToShortDTO(comment);
    }

    @Override
    public CommentShortResponse updateComment(Long postId, Long commentId, CommentRequest request) throws UnauthorizedException {
        log.info("Update comment in post with id: {}", postId);
        if(!postRepository.existsById(postId)){
            log.error("Post with id: {} not found", postId);
            throw new ResourceNotFoundException(messageConfig.getMessage(MessageError.POST_NOT_FOUND, postId));
        }
        Comment updatedComment = commentRepository.findById(commentId).orElseThrow(() ->
                new ResourceNotFoundException(messageConfig.getMessage(MessageError.COMMENT_NOT_FOUND, commentId)));
        if(userService.getCurrentUser().equals(updatedComment.getUser())) {
            if(request.getContent() != null){
                updatedComment.setCommentDetail(request.getContent());
            } else updatedComment.setCommentDetail(updatedComment.getCommentDetail());
            updatedComment.setParent(updatedComment.getParent());
            updatedComment.setPost(updatedComment.getPost());
            commentRepository.save(updatedComment);
            return convertCommentToShortDTO(updatedComment);
        }
        else {
            log.error(messageConfig.getMessage(MessageError.ACCESS_DENIED));
            throw new UnauthorizedException(messageConfig.getMessage(MessageError.ACCESS_DENIED));
        }
    }

    @Override
    public PageResponse<CommentShortResponse> getComments(Pageable pageable) {
        log.info("Getting total comments!");
        Page<Comment> comments = commentRepository.findAll(pageable);
        Page<CommentShortResponse> commentPage = comments.map(this::convertCommentToShortDTO);
        log.info("Total comments: {}", commentPage.getTotalElements());
        return new PageResponse<>(
                commentPage.getNumber() + 1,
                commentPage.getNumberOfElements(),
                commentPage.getTotalPages(),
                commentPage.getContent()
        );
    }

    @Override
    public CommentShortResponse getComment(Long id) {
        log.info("Getting comment with id: {}", id);
        Optional<Comment> commentOptional = commentRepository.findById(id);
        if(commentOptional.isPresent()) {
            Comment comment = commentOptional.get();
            return convertCommentToShortDTO(comment);
        }
        else{
            log.info("Comment with id: {} not found", id);
            throw new ResourceNotFoundException(messageConfig.getMessage(MessageError.COMMENT_NOT_FOUND, id));
        }
    }

    @Override
    public void deleteComment(Long id){
        log.info("Deleting comment with id: {}", id);
        Comment commentDeleted = commentRepository.findById(id).orElseThrow(() ->
        {
            log.error("Comment with id: {} not found", id);
            return new ResourceNotFoundException(messageConfig.getMessage(MessageError.COMMENT_NOT_FOUND, id));
        });
        Post post = commentDeleted.getPost();
        User user = commentDeleted.getUser();
        User currentUser = userService.getCurrentUser();
        if(currentUser.getRole().getRoleName().equals(RoleType.ADMIN) ||
                currentUser.equals(commentDeleted.getUser()) || currentUser.equals(commentDeleted.getPost().getUser())){
            List<Comment> comments = commentRepository.findAllByParent_CommentId(id);
            comments.forEach(c -> { if(commentDeleted.getParent() != null)
            {
                c.setParent(commentDeleted.getParent()); commentRepository.save(c);
            }
            else {c.setParent(null); commentRepository.save(c);}
            });
            post.getComments().remove(commentDeleted);
            postRepository.save(post);
            user.getComments().remove(commentDeleted);
            userRepository.save(user);
            commentRepository.delete(commentDeleted);
        }
        log.info("Comment with id: {} has been deleted", id);
    }

    @Override
    public CommentResponse convertCommentToDTO(Comment comment){
        CommentResponse commentResponse = new CommentResponse();
        commentResponse.setCommentId(comment.getCommentId());
        commentResponse.setCreatedAt(comment.getCreatedTime());
        commentResponse.setCommentDetail(comment.getCommentDetail());
        commentResponse.setUpdatedAt(comment.getUpdatedTime());
        commentResponse.setUserComment(comment.getUser().getUserName());
        if(comment.getParent() != null) {
            commentResponse.setParentId(comment.getParent().getCommentId());
        }
        commentResponse.setReplies(new ArrayList<>());
        commentResponse.setLikes(comment.getLikesCount());
        commentResponse.setDislikes(comment.getDislikesCount());
      //  commentResponse.setPostId(comment.getPost().getPostId());
        return commentResponse;
    }

    @Override
    public CommentShortResponse convertCommentToShortDTO(Comment comment){
        CommentShortResponse commentResponse = new CommentShortResponse();
        commentResponse.setCreatedAt(comment.getCreatedTime());
        commentResponse.setCommentDetail(comment.getCommentDetail());
        commentResponse.setUpdatedAt(comment.getUpdatedTime());
        commentResponse.setUserComment(comment.getUser().getUserName());
        commentResponse.setCommentLikes(comment.getLikesCount());
        commentResponse.setCommentId(comment.getCommentId());
        commentResponse.setCommentDislikes(comment.getDislikesCount());
        commentResponse.setPostId(comment.getPost().getPostId());
        return commentResponse;
    }



}

*/
