package com.example.projectiii.dto.response.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserViewResponse {
    private Integer id;
    private String userName;
    private String fullName;
    private String gmail;
    private String imageUrl;
    private String cloudinaryImageId;
}
