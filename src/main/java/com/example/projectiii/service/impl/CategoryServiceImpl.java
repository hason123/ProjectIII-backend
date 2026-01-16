package com.example.projectiii.service.impl;

import com.example.projectiii.config.MessageConfig;
import com.example.projectiii.constant.MessageError;
import com.example.projectiii.dto.request.CategoryRequest;
import com.example.projectiii.dto.response.CategoryResponse;
import com.example.projectiii.dto.response.PageResponse;
import com.example.projectiii.entity.Category;
import com.example.projectiii.exception.ResourceNotFoundException;
import com.example.projectiii.repository.BookRepository;
import com.example.projectiii.repository.CategoryRepository;
import com.example.projectiii.service.CategoryService;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
@Slf4j
@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final BookRepository bookRepository;
    private final MessageConfig messageConfig;

    public CategoryServiceImpl(CategoryRepository categoryRepository, BookRepository bookRepository, MessageConfig messageConfig) {
        this.categoryRepository = categoryRepository;
        this.bookRepository = bookRepository;
        this.messageConfig = messageConfig;
    }

    @Override
    public CategoryResponse getCategory(Integer id) {
        log.info("Getting category with id {}", id);
        Category category = categoryRepository.findById(id).orElse(null);
        if(category == null){
            log.error("Category with id {} not found", id);
            throw new ResourceNotFoundException(messageConfig.getMessage(MessageError.CATEGORY_NOT_FOUND,id));
        }
        log.info("Returning category {}", category);
        return convertEntityToDTO(category);
    }

    @Override
    public CategoryResponse addCategory(CategoryRequest request) {
        Category category = new Category();
        if(categoryRepository.existsByCategoryName(request.getCategoryName())){
            throw new DataIntegrityViolationException(messageConfig.getMessage(MessageError.CATEGORY_NAME_UNIQUE));
        }
        else category.setCategoryName(request.getCategoryName());
        category.setDescription(request.getDescription());
        categoryRepository.save(category);
        return convertEntityToDTO(category);
    }

    @Override
    public CategoryResponse updateCategory(Integer id, CategoryRequest request) {
        log.info("Updating category with id {}", id);
        Category updatedCategory = categoryRepository.findById(id).orElse(null);
        if(updatedCategory == null){
            log.error("Category with id {} not found", id);
            throw new ResourceNotFoundException(messageConfig.getMessage(MessageError.CATEGORY_NOT_FOUND,id));
        }
        if(request.getCategoryName() != null){
            if(categoryRepository.existsByCategoryName(request.getCategoryName())){
                updatedCategory.setCategoryName(updatedCategory.getCategoryName());
            }
            else updatedCategory.setCategoryName(request.getCategoryName());
        }
        if(request.getDescription() != null){
            updatedCategory.setDescription(request.getDescription());
        }
        else updatedCategory.setDescription(updatedCategory.getDescription());
        categoryRepository.save(updatedCategory);
        log.info("Updated successfully");
        return convertEntityToDTO(updatedCategory);
    }

    @Override
    public void deleteCategory(Integer id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(messageConfig.getMessage(MessageError.CATEGORY_NOT_FOUND, id)));
        category.getBooks().forEach(book ->
        {
            book.getCategories().remove(category);
            bookRepository.save(book);
        } );
        categoryRepository.delete(category);
    }

    @Override
    public PageResponse<CategoryResponse> getAllCategories(Pageable pageable) {
        log.info("Getting category's page");
        Page<Category> categories = categoryRepository.findAll(pageable);
        Page<CategoryResponse> categoryDTO = categories.map(this::convertEntityToDTO);
        //Page<CategoryResponse> categoryDTO = categories.map(category -> convertEntityToDTO(category));
        PageResponse<CategoryResponse> categoryPage = new PageResponse<>(
                categoryDTO.getNumber() + 1,
                categoryDTO.getTotalPages(),
                categoryDTO.getTotalElements(),
                categoryDTO.getContent()
        );
        log.info("Returning category page!");
        return categoryPage;
    }

    @Override
    public void createCategoryWorkbook(HttpServletResponse response) throws IOException {
        log.info("Creating category workbook");
        List<Object[]> result = categoryRepository.findCategoryAndBookCount();
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Category Report");
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("STT");
        header.createCell(1).setCellValue("Tên thể loại");
        header.createCell(2).setCellValue("Số lượng sách");
        int rowNum = 1; int index = 1;
        //co ve Object[] luu nhieu lieu du lieu khac nhau
        for (Object[] record : result) {
            Category category = (Category) record[0];
            Long bookCount = (Long) record[1];
            Row excelRow = sheet.createRow(rowNum++);
            excelRow.createCell(0).setCellValue(index++);
            excelRow.createCell(1).setCellValue(category.getCategoryName());
            excelRow.createCell(2).setCellValue(bookCount);
        }
        response.setHeader("Content-Type", "attachment; filename=borrowing.xlsx");
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
        log.info("Successfully created category workbook");
    }

    public CategoryResponse convertEntityToDTO(Category category) {
        CategoryResponse categoryDTO = new CategoryResponse();
        categoryDTO.setCategoryName(category.getCategoryName());
        categoryDTO.setCategoryId(category.getCategoryId());
        categoryDTO.setDescription(category.getDescription());
        List<CategoryResponse.BookBasic> bookBasics =
            Optional.ofNullable(category.getBooks())
                    .orElseGet(Collections::emptyList)
                    .stream()
                    .map(book -> new CategoryResponse.BookBasic(book.getBookId(), book.getBookName()))
                    .collect(Collectors.toList());
        categoryDTO.setBooks(bookBasics);
        return categoryDTO;
    }






}
