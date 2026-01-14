package com.example.projectiii.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="book")
@SQLDelete(sql = "UPDATE book SET is_deleted = true WHERE book_id = ?")
@SQLRestriction(value = "is_deleted = false")
public class Book extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_id")
    private Integer bookId;
    @Column(name = "book_Name")
    private String bookName;
    @Column(name = "author")
    private String author;
    @Column(name = "publisher")
    private String publisher;
    @Column(name = "page_count")
    private Integer pageCount;
    @Column(name = "print_Type")
    private String printType;
    @Column(name = "language")
    private String language;
    @Column(name = "quantity")
    private Integer quantity;
    @Column(name = "book_desc", columnDefinition = "MEDIUMTEXT")
    private String bookDesc;
    @Column(name = "image_url", columnDefinition = "MEDIUMTEXT")
    private String imageUrl;
    @Column(name = "cloudinary_image_id")
    private String cloudinaryImageId;
    private Double ratings;
    @ManyToMany
    @JoinTable(
            name = "book_category",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private List<Category> categories;
    @OneToMany(mappedBy = "book")
    private List<Borrowing> borrowings;
    @OneToMany(mappedBy = "book")
    private List<Comment> comments;

}
