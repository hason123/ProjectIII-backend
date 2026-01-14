package com.example.projectiii.dto.response.comment;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CommentShortResponse {
    private Integer commentId;
    private String commentDetail;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private String userComment;
    private Integer commentLikes;
    private Integer postId;
    private Integer commentDislikes;

}
