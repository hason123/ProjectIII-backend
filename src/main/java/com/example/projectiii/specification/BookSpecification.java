package com.example.projectiii.specification;

import com.example.projectiii.entity.Book;
import com.example.projectiii.entity.Category;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;
import java.util.List;

public class BookSpecification {
    public static Specification<Book> likeName(String name) {
        return (root, query, cb)
                -> cb.like(root.get("bookName"), "%" + name.toLowerCase() + "%");
    }
   public static Specification<Book> likeAuthor(String author) {
        return (root,query, cb)
                -> cb.like(root.get("author"), "%" + author.toLowerCase() + "%");
    }
    public static Specification<Book> likePublisher(String publisher) {
        return (root,query, cb)
                -> cb.like(root.get("publisher"), "%" + publisher.toLowerCase()+ "%");
    }
    public static Specification<Book> hasId(Integer id) {
        return (root, query, cb)
                -> cb.equal(root.get("bookId"), id);
    }
    public static Specification<Book> likePrintType(String printType) {
        return (root,query, cb)
                -> cb.like(root.get("printType"), "%" + printType.toLowerCase() + "%");
    }
    public static Specification<Book> likeLanguage(String language) {
        return (root,query, cb)
                -> cb.like(root.get("language"), "%" + language + "%");
    }
    public static Specification<Book> lessThanPageCount(Integer pageCount) {
        return (root,query, cb)
                -> cb.greaterThanOrEqualTo(root.get("pageCount"), pageCount);
    }
    public static Specification<Book> moreThanPageCount(Integer pageCount) {
        return (root, query, cb)
                -> cb.lessThanOrEqualTo(root.get("pageCount"), pageCount);
    }
    public static Specification<Book> lessThanEqualQuantity(Integer quantity) {
        return (root,query, cb)
                -> cb.greaterThanOrEqualTo(root.get("quantity"), quantity);
    }
    public static Specification<Book> moreThanEqualQuantity(Integer quantity) {
        return (root, query, cb)
                -> cb.lessThanOrEqualTo(root.get("quantity"), quantity);
    }
    public static Specification<Book> hasBookWithCategories(List<String> categories) {
        return (root, query, cb) -> {
            Join<Book, Category> categoryJoin = root.join("categories");
            return categoryJoin.get("categoryName").in(categories);
        };
    }









}
