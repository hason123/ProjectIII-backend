package com.example.projectiii.dto.request.search;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SearchUserRequest {
    private String userName;
    private Long userId;
    private String roleName;
   // private LocalDate birthday;
}
