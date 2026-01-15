package com.example.projectiii.repository;

import com.example.projectiii.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    @Query("SELECT c, COUNT(b) FROM Category c LEFT JOIN c.books b GROUP BY c")
    List<Object[]> findCategoryAndBookCount();

    Optional<Category> findByCategoryName(String catName);

    boolean existsByCategoryName(String catName);

}
