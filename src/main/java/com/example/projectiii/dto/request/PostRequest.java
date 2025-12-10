package com.example.projectiii.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostRequest {
    @NotBlank(message = "{error.post.title.null}")
    private String title;
    @NotBlank(message = "{error.post.content.null}")
    private String content;
}
