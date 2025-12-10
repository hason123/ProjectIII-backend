package com.example.projectiii.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PermissionRequest {
    @NotBlank(message = "{error.permission.name.null")
    private String name;
    @NotEmpty(message = "{error.permission.apiPath.null")
    private String apiPath;
    @NotEmpty(message = "{error.permission.method.null")
    private String method;
    private List<Long> roleIds;
    private String description;
}
