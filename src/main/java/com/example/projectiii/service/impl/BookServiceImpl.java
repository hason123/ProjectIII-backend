package com.example.projectiii.service.impl;

import com.example.projectiii.config.MessageConfig;
import com.example.projectiii.constant.MessageError;
import com.example.projectiii.dto.request.BookRequest;
import com.example.projectiii.dto.request.search.SearchBookRequest;
import com.example.projectiii.dto.response.BookResponse;
import com.example.projectiii.dto.response.CloudinaryResponse;
import com.example.projectiii.dto.response.PageResponse;
import com.example.projectiii.entity.Book;
import com.example.projectiii.entity.Category;
import com.example.projectiii.exception.ResourceNotFoundException;
import com.example.projectiii.repository.BookRepository;
import com.example.projectiii.repository.CategoryRepository;
import com.example.projectiii.service.BookService;
import com.example.projectiii.service.CloudinaryService;
import com.example.projectiii.specification.BookSpecification;
import com.example.projectiii.utils.FileUploadUtil;
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
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
@Slf4j
@Service
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final MessageConfig messageConfig;
    private final CloudinaryService cloudinaryService;

    public BookServiceImpl(BookRepository bookRepository, CategoryRepository categoryRepository, MessageConfig messageConfig, CloudinaryService cloudinaryService) {
        this.bookRepository = bookRepository;
        this.categoryRepository = categoryRepository;
        this.messageConfig = messageConfig;
        this.cloudinaryService = cloudinaryService;
    }

    @Override
    public BookResponse addBook(BookRequest request) {
        log.info("Adding new book");
        Book book = new Book();
        book.setAuthor(request.getAuthor());
        book.setBookDesc(request.getBookDesc());
        if(bookRepository.existsByBookName(request.getBookName())){
            throw new DataIntegrityViolationException(messageConfig.getMessage(MessageError.BOOK_NAME_UNIQUE));
        }
        else book.setBookName(request.getBookName());
        book.setPageCount(request.getPageCount());
        book.setPublisher(request.getPublisher());
        book.setQuantity(request.getQuantity());
        if (request.getCategoryIDs() != null) {
            List<Category> categories = request.getCategoryIDs().stream()
                    .map(categoryID -> categoryRepository.findById(categoryID)
                            .orElseThrow(() ->
                            {
                                log.error("Category with id: {} not found", categoryID);
                                return new ResourceNotFoundException(messageConfig.getMessage(MessageError.CATEGORY_NOT_FOUND,categoryID));
                            }))
                    .collect(Collectors.toList()); ;
            book.setCategories(categories);
        }
        bookRepository.save(book);
        log.info("Book with id: {} has been added", book.getBookId());
        return convertBookToDTO(book);
    }

    @Override
    public BookResponse getBookById(Integer id) {
        log.info("Getting book with id: {}", id);
        Book book = bookRepository.findById(id).orElse(null);
        if(book == null){
            log.error("Book with id: {} not found", id);
            throw new ResourceNotFoundException(messageConfig.getMessage(MessageError.BOOK_NOT_FOUND,id));
        }
        log.info("Return book with id {}", book.getBookId());
        return convertBookToDTO(book);
    }

    @Override
    public void deleteBookById(Integer id) {
        log.info("Deleting book with id: {}", id);
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> {
                    log.error(messageConfig.getMessage(MessageError.BOOK_NOT_FOUND, id));
                    return new ResourceNotFoundException(messageConfig.getMessage(MessageError.BOOK_NOT_FOUND, id));
                });
        book.getCategories().forEach(category -> category.getBooks().remove(book));
        bookRepository.deleteById(id);
        log.info("Book with id: {} has been deleted", id);
    }

    @Override
    public BookResponse updateBook(Integer id, BookRequest request) {
        log.info("Updating book with id: {}", id);
        Book updatedBook = bookRepository.findById(id).orElse(null);
        if(updatedBook == null){
            log.error(messageConfig.getMessage(MessageError.BOOK_NOT_FOUND,id));
            throw new ResourceNotFoundException(messageConfig.getMessage(MessageError.BOOK_NOT_FOUND,id));
        }
        if (request.getAuthor() != null) {
            updatedBook.setAuthor(request.getAuthor());
        }
        else updatedBook.setAuthor(updatedBook.getAuthor());
        if (request.getBookDesc() != null) {
            updatedBook.setBookDesc(request.getBookDesc());
        }
        if (request.getBookName() != null) {
            if(bookRepository.existsByBookName(request.getBookName())){
                updatedBook.setBookName(updatedBook.getBookName());
            }
            updatedBook.setBookName(request.getBookName());
        }
        else  updatedBook.setBookName(updatedBook.getBookName());
        if (request.getPageCount() != null) {
            updatedBook.setPageCount(request.getPageCount());
        }
        if (request.getPublisher() != null) {
            updatedBook.setPublisher(request.getPublisher());
        }
        else updatedBook.setPublisher(updatedBook.getPublisher());
        if (request.getQuantity() != null) {
            updatedBook.setQuantity(request.getQuantity());
        }
        else updatedBook.setQuantity(updatedBook.getQuantity());
        if (request.getCategoryIDs() != null) {
            List<Category> categories = request.getCategoryIDs().stream()
                    .map(categoryID -> categoryRepository.findById(categoryID)
                            .orElseThrow(() ->
                            {
                                log.error(messageConfig.getMessage(MessageError.CATEGORY_NOT_FOUND,categoryID));
                                return new ResourceNotFoundException(messageConfig.getMessage(MessageError.CATEGORY_NOT_FOUND,categoryID));
                            }))
                    .collect(Collectors.toList()); ;
            updatedBook.setCategories(categories);
        }
        else updatedBook.setCategories(updatedBook.getCategories());
        bookRepository.save(updatedBook);
        return convertBookToDTO(updatedBook);
    }

    @Override
    public PageResponse<BookResponse> searchBooks(SearchBookRequest request, Pageable pageable) {
        log.info("Searching books with request: {}", request);
        String bookName = request.getBookName();
        String author = request.getAuthor();
        Integer bookId = request.getBookId();
        String language = request.getLanguage();
        String printType = request.getPrintType();
        String publisher = request.getPublisher();
        Integer minQuantity = request.getMinQuantity();
        Integer maxQuantity = request.getMaxQuantity();
        Integer minPageCount = request.getMinPageCount();
        Integer maxPageCount = request.getMaxPageCount();
        List<String> categories = request.getCategories();

        Specification<Book> spec = (root, query, cb) -> cb.conjunction();

        if (StringUtils.hasText(bookName)) {
            spec = spec.and(BookSpecification.likeName(bookName));
        }
        if (StringUtils.hasText(author)) {
            spec = spec.and(BookSpecification.likeAuthor(author));
        }
        if (bookId != null) {
            spec = spec.and(BookSpecification.hasId(bookId));
        }
        if (StringUtils.hasText(language)) {
            spec = spec.and(BookSpecification.likeLanguage(language));
        }
        if (StringUtils.hasText(printType)) {
            spec = spec.and(BookSpecification.likePrintType(printType));
        }
        if (StringUtils.hasText(publisher)) {
            spec = spec.and(BookSpecification.likePublisher(publisher));
        }
        if (minPageCount != null) {
            spec = spec.and(BookSpecification.moreThanPageCount(minPageCount));
        }
        if (maxPageCount != null) {
            spec = spec.and(BookSpecification.lessThanPageCount(maxPageCount));
        }
        if (minQuantity != null) {
            spec = spec.and(BookSpecification.moreThanEqualQuantity(minQuantity));
        }
        if (maxQuantity != null) {
            spec = spec.and(BookSpecification.lessThanEqualQuantity(maxQuantity));
        }
        if (categories != null && !categories.isEmpty()) {
            spec = spec.and(BookSpecification.hasBookWithCategories(categories));
        }

        Page<Book> bookPage = bookRepository.findAll(spec, pageable);
        Page<BookResponse> bookResponse = bookPage.map(this::convertBookToDTO);
        log.info("Returning books with {} books found", bookResponse.getTotalElements());
        return new PageResponse<>(
                bookResponse.getNumber() + 1,
                bookResponse.getTotalPages(),
                bookResponse.getNumberOfElements(),
                bookResponse.getContent()
        );
    }

    @Override
    public PageResponse<BookResponse> getBookPage(Pageable pageable) {
        log.info("Getting book page with pageable: {}", pageable);
        Page<Book> bookPage = bookRepository.findAll(pageable);
        Page<BookResponse> bookResponse = bookPage.map(this::convertBookToDTO);
        PageResponse<BookResponse> pageDTO = new PageResponse<>(
                bookResponse.getNumber() + 1,
                bookResponse.getTotalPages(),
                bookResponse.getNumberOfElements(),
                bookResponse.getContent()
                );
        log.info("Returning books with {} books found", pageDTO.getTotalElements());
        return pageDTO;
    }

    @Transactional
    public CloudinaryResponse uploadImage(final Integer id, final MultipartFile file) {
        final Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        final String cloudinaryImageId = book.getCloudinaryImageId();
        if(StringUtils.hasText(cloudinaryImageId)) {
           cloudinaryService.deleteFile(cloudinaryImageId);
        }
        FileUploadUtil.assertAllowed(file, FileUploadUtil.IMAGE_PATTERN);
        final String fileName = FileUploadUtil.getFileName(file.getOriginalFilename());
        final CloudinaryResponse response = this.cloudinaryService.uploadFile(file, fileName);
        book.setImageUrl(response.getUrl());
        book.setCloudinaryImageId(response.getPublicId());
        bookRepository.save(book);
        return response;
    }

    @Override
    public void exportBookWorkbook(HttpServletResponse response) throws IOException {
        log.info("Creating book workbook");
        List<Book> books = bookRepository.findAll();
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Book Report");
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("ID");
        header.createCell(1).setCellValue("Tên sách");
        header.createCell(2).setCellValue("Tác giả");
        header.createCell(3).setCellValue("Nhà xuất bản");
        header.createCell(4).setCellValue("Số trang");
        header.createCell(5).setCellValue("Loại in");
        header.createCell(6).setCellValue("Ngôn ngữ");
        header.createCell(7).setCellValue("Thể loại");
        header.createCell(8).setCellValue("Số lượng sách");
        int rowNum = 1;
        for (Book book : books) {
            Row excelRow = sheet.createRow(rowNum++);
            excelRow.createCell(0).setCellValue(book.getBookId());
            excelRow.createCell(1).setCellValue(book.getBookName());
            excelRow.createCell(2).setCellValue(book.getAuthor());
            excelRow.createCell(3).setCellValue(book.getPublisher());
            excelRow.createCell(4).setCellValue(book.getPageCount());
            excelRow.createCell(5).setCellValue(book.getPrintType());
            excelRow.createCell(6).setCellValue(book.getLanguage());
            List<Category> categories = book.getCategories();
            String categoryNames = categories.stream()
                    .map(Category::getCategoryName)
                    .collect(Collectors.joining(", "));
            excelRow.createCell(7).setCellValue(categoryNames);
            excelRow.createCell(8).setCellValue(book.getQuantity());
        }
        response.setHeader("Content-Type", "attachment; filename=borrowing.xlsx");
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
        log.info("Successfully created category workbook");
    }

    @Override
    public void importExcel(final MultipartFile file) throws IOException {
        Workbook workbook = new XSSFWorkbook(file.getInputStream());
        final Sheet sheet = workbook.getSheetAt(0);
        final List<Book> validBooks = new ArrayList<>();
        for (final Row row : sheet) {
            if (row.getRowNum() == 0) continue; // skip header
            String bookName = row.getCell(0).getStringCellValue();
            String author = row.getCell(1).getStringCellValue();
            String publisher = row.getCell(2).getStringCellValue();
            Integer pageCount = (int) row.getCell(3).getNumericCellValue();
            String printType = row.getCell(4).getStringCellValue();
            String language = row.getCell(5).getStringCellValue();
            Integer quantity = (int) row.getCell(6).getNumericCellValue();
            String bookDesc = row.getCell(7).getStringCellValue();
            String categoryIdsStr = row.getCell(8).getStringCellValue(); // hoặc getNumericCellValue nếu chắc chắn là số
            String[] categoryIdArr = categoryIdsStr.split(",");
            // Validate category
            List<Category> categories = new ArrayList<>();
            for (String catIdStr : categoryIdArr) {
                int catId;
                try {
                    catId = Integer.parseInt(catIdStr.trim());
                } catch (NumberFormatException e) {
                    continue;
                }
                Category category = categoryRepository.findById(catId).orElse(null);
                if (category == null || bookRepository.existsByBookName(bookName)) continue;
                categories.add(category);
                Book book = new Book();
                book.setBookName(bookName);
                book.setAuthor(author);
                book.setPublisher(publisher);
                book.setPageCount(pageCount);
                book.setPrintType(printType);
                book.setLanguage(language);
                book.setQuantity(quantity);
                book.setBookDesc(bookDesc);
                book.setCategories(categories);
                validBooks.add(book);
            }
        }
        bookRepository.saveAll(validBooks);
    }

    public BookResponse convertBookToDTO(Book book) {
        BookResponse bookDTO = new BookResponse();
        bookDTO.setBookId(book.getBookId());
        bookDTO.setAuthor(book.getAuthor());
        bookDTO.setBookDesc(book.getBookDesc());
        bookDTO.setBookName(book.getBookName());
        bookDTO.setPageCount(book.getPageCount());
        bookDTO.setPublisher(book.getPublisher());
        bookDTO.setQuantity(book.getQuantity());
        bookDTO.setPrintType(book.getPrintType());
        bookDTO.setLanguage(book.getLanguage());
        bookDTO.setBookDesc(book.getBookDesc());
        bookDTO.setImageUrl(book.getImageUrl());
        bookDTO.setCloudinaryImageId(book.getCloudinaryImageId());
        List<BookResponse.CategoryDTO> categoryDTOs =
                Optional.ofNullable(book.getCategories())
                        .orElseGet(Collections::emptyList)
                        .stream()
                        .map(c -> new BookResponse.CategoryDTO(c.getCategoryId(), c.getCategoryName()))
                        .collect(Collectors.toList());
        bookDTO.setCategories(categoryDTOs);
        return bookDTO;
    }
}
