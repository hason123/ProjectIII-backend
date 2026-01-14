package com.example.projectiii.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name= "users")
@SQLDelete(sql = "UPDATE users SET is_deleted = true WHERE user_id = ?")
@SQLRestriction(value = "is_deleted = false") //mac dinh chi lay nhung ban gi ko bi soft delete
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer id;
    @Column(name = "user_name", unique = true)
    private String userName;
    @Column(name = "pass_word")
    private String password;
    @Column(name = "full_name")
    private String fullName;
    @Column(name = "phone_number")
    private String phoneNumber;
    @Column(name = "birthday")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate birthday;
    @Column(name = "address")
    private String address;
    @Column(name = "refresh_token", columnDefinition = "MEDIUMTEXT")
    private String refreshToken;
    @Column(name = "gmail")
    private String gmail;
    @Column(name = "is_verified", nullable = false)
    private boolean isVerified = false;
    @Column(name = "image_url", columnDefinition = "MEDIUMTEXT")
    private String imageUrl;
    @Column(name = "cloudinary_image_id")
    private String cloudinaryImageId;
    @ManyToOne
    @JoinColumn(name = "user_role_id")
    private Role role;
    @OneToMany(mappedBy = "user")
    private List<Comment> comments;
    @OneToMany(mappedBy = "user")
    private List<Borrowing> borrowings;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Otp> otps = new ArrayList<>();
    @OneToMany(mappedBy = "recipient")
    private List<Notification> notifications = new ArrayList<>();

}
