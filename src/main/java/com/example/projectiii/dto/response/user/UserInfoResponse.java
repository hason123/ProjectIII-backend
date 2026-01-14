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
public class UserInfoResponse {
    private Integer id;
    private String userName;
    //private String password;
    private String fullName;
    private String phoneNumber;
    private LocalDate birthday;
    private String address;
    private String roleName;
    private String gmail;
    private String imageUrl;
    private String cloudinaryImageId;
}
