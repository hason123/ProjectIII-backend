package com.example.projectiii.service.impl;

import com.example.projectiii.config.MessageConfig;
import com.example.projectiii.constant.BorrowingStatus;
import com.example.projectiii.constant.MessageError;
import com.example.projectiii.dto.request.BorrowingRequest;
import com.example.projectiii.dto.response.BorrowingResponse;
import com.example.projectiii.dto.response.PageResponse;
import com.example.projectiii.entity.Book;
import com.example.projectiii.entity.Borrowing;
import com.example.projectiii.entity.User;
import com.example.projectiii.exception.BusinessException;
import com.example.projectiii.exception.ResourceNotFoundException;
import com.example.projectiii.repository.BookRepository;
import com.example.projectiii.repository.BorrowingRepository;
import com.example.projectiii.repository.UserRepository;
import com.example.projectiii.service.BorrowingService;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
public class BorrowingServiceImpl implements BorrowingService {
    private final BorrowingRepository borrowingRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final MessageConfig messageConfig;

    public BorrowingServiceImpl(BorrowingRepository borrowingRepository, BookRepository bookRepository, UserRepository userRepository, MessageConfig messageConfig) {
        this.borrowingRepository = borrowingRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.messageConfig = messageConfig;
    }

    @Override
    public BorrowingResponse getBorrowingById(Integer id) {
        Borrowing borrowing = borrowingRepository.findById(id).orElseThrow(() ->
        {
            return new ResourceNotFoundException(messageConfig.getMessage(MessageError.BORROWING_NOT_FOUND, id));
        });
        return convertBorrowingToDTO(borrowing);
    }

    @Override
    public void deleteBorrowingById(Integer id) {
        Borrowing borrowing = borrowingRepository.findById(id).orElseThrow(() ->
        {
            return new ResourceNotFoundException(messageConfig.getMessage(MessageError.BORROWING_NOT_FOUND, id));
        });
        if (borrowing.getReturnedDate() != null) {
            borrowing.getBook().setQuantity(borrowing.getBook().getQuantity() + 1);
            bookRepository.save(borrowing.getBook());
        }
        borrowingRepository.delete(borrowing);
    }

    @Override
    @Transactional
    public BorrowingResponse addBorrowing(BorrowingRequest request) {
        User userAdded = userRepository.findById(request.getUserId()).orElseThrow(() ->
        {
            return new ResourceNotFoundException(messageConfig.getMessage(MessageError.USER_NOT_FOUND , request.getUserId()));
        });
        Book bookAdded = bookRepository.findById(request.getBookId()).orElseThrow(() ->
        {
            return new ResourceNotFoundException(messageConfig.getMessage(MessageError.BOOK_NOT_FOUND, request.getBookId()));
        });
        if(bookAdded.getQuantity() <= 0){
            log.error(messageConfig.getMessage(MessageError.BOOK_OUT_OF_STOCK));
            throw new BusinessException (messageConfig.getMessage(MessageError.BOOK_OUT_OF_STOCK, request.getBookId()));
        }
        Borrowing borrowing = new Borrowing();
        log.info("Save user borrowing");
        borrowing.setUser(userAdded);
        log.info("Save book borrowing");
        borrowing.setBook(bookAdded);
        borrowing.setBorrowDate(request.getBorrowingDate());
        if(request.getReturnDate()!=null){
            if(request.getReturnDate().isBefore(request.getBorrowingDate())) {
                throw new BusinessException(messageConfig.getMessage(MessageError.BORROWING_RETURNDATE_INVALID));
            }
            borrowing.setReturnedDate(request.getReturnDate());
            if(request.getReturnDate().isAfter(request.getBorrowingDate().plusMonths(1))) {
                borrowing.setStatus(BorrowingStatus.OVERDUE);
            }
            else borrowing.setStatus(BorrowingStatus.RETURNED);
        }
        else {
            if(LocalDate.now().isAfter(request.getBorrowingDate().plusMonths(1))) {
                borrowing.setStatus(BorrowingStatus.OVERDUE);
            }
            else{
                borrowing.setStatus(BorrowingStatus.BORROWING);
            }
            bookAdded.setQuantity(bookAdded.getQuantity() - 1);
            bookRepository.save(bookAdded);
        }
        /*if (checkDuplicate(borrowing)) {
            log.error(messageConfig.getMessage(MessageError.BORROWING_WRONG_DATE));
            throw new BusinessException(messageConfig.getMessage(MessageError.BORROWING_WRONG_DATE));
        }*/
        borrowingRepository.save(borrowing);
        return convertBorrowingToDTO(borrowing);
    }

    @Override
    @Transactional
    public BorrowingResponse updateBorrowing(Integer id, BorrowingRequest request) {
        log.info("Update borrowing with id {}", id);
        Borrowing borrowing = borrowingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(messageConfig.getMessage(MessageError.BORROWING_NOT_FOUND, id)));
        BorrowingStatus prevStatus = borrowing.getStatus();
        if (request.getUserId() != null) {
            User user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException(messageConfig.getMessage(MessageError.USER_NOT_FOUND, request.getUserId())));
            borrowing.setUser(user);
        }
        else borrowing.setUser(borrowing.getUser());
        // Borrow date update (only if provided)
        if (request.getBorrowingDate() != null) {
            borrowing.setBorrowDate(request.getBorrowingDate());
        }
        else borrowing.setBorrowDate(borrowing.getBorrowDate());
        // Return date update (only if provided)
        if (request.getReturnDate() != null) {
            if (request.getBorrowingDate() != null) {
                if (request.getReturnDate().isBefore(request.getBorrowingDate())) {
                    throw new BusinessException(messageConfig.getMessage(MessageError.BORROWING_WRONG_DATE));
                }
            } else {
                if (request.getReturnDate().isBefore(borrowing.getBorrowDate())) {
                    throw new BusinessException(messageConfig.getMessage(MessageError.BORROWING_WRONG_DATE));
                }
            }
            borrowing.setReturnedDate(request.getReturnDate());
            if (request.getReturnDate().isAfter(
                    (request.getBorrowingDate() != null ? request.getBorrowingDate().plusMonths(1) : borrowing.getBorrowDate()).plusMonths(1)
            )) {
                borrowing.setStatus(BorrowingStatus.OVERDUE);
            } else {
                borrowing.setStatus(BorrowingStatus.RETURNED);
            }
        }
        else{
            borrowing.setReturnedDate(null);
            if (LocalDate.now().isAfter(request.getBorrowingDate().plusMonths(1))) {
                borrowing.setStatus(BorrowingStatus.OVERDUE);
            } else {
                borrowing.setStatus(BorrowingStatus.BORROWING);
            }
        }
        if (request.getBookId() != null) {
            Book oldBook = borrowing.getBook();
            Book newBook = bookRepository.findById(request.getBookId())
                    .orElseThrow(() -> new ResourceNotFoundException(messageConfig.getMessage(MessageError.BOOK_NOT_FOUND, request.getBookId())));
            if (!oldBook.equals(newBook)) {
                if (newBook.getQuantity() <= 0) {
                    log.error(messageConfig.getMessage(MessageError.BOOK_OUT_OF_STOCK));
                    throw new BusinessException(messageConfig.getMessage(MessageError.BOOK_OUT_OF_STOCK, request.getBookId()));
                }
                if(borrowing.getStatus() != BorrowingStatus.RETURNED) {
                    oldBook.setQuantity(oldBook.getQuantity() + 1);
                    bookRepository.save(oldBook);
                    newBook.setQuantity(newBook.getQuantity() - 1);
                    bookRepository.save(newBook);
                }
                borrowing.setBook(newBook);
            }
            else borrowing.setBook(oldBook);
        }
        else borrowing.setBook(borrowing.getBook());

        /*if (checkDuplicate(borrowing)) {
            throw new BusinessException(messageConfig.getMessage(MessageError.BORROWING_WRONG_DATE));
        }*/

        // Only handle returned status if status changed
        if (borrowing.getStatus() == BorrowingStatus.RETURNED && prevStatus != BorrowingStatus.RETURNED) {
            Book returnedBook = borrowing.getBook();
            returnedBook.setQuantity(returnedBook.getQuantity() + 1);
            bookRepository.save(returnedBook);
        }
        borrowingRepository.save(borrowing);
        log.info("Successfully updated borrowing with id {}", id);
        return convertBorrowingToDTO(borrowing);
    }

   /* @Override
    public PageResponse<BorrowingResponse> getBorrowingPage(Pageable pageable) {
        Page<Borrowing> borrowingPage = borrowingRepository.findAllCustomSort(pageable);
        Page<BorrowingResponse> borrowingResponseDTO = borrowingPage.map(this::convertBorrowingToDTO);
        return new PageResponse<>(
                borrowingResponseDTO.getNumber() + 1,
                borrowingResponseDTO.getNumberOfElements(),
                borrowingResponseDTO.getTotalPages(),
                borrowingResponseDTO.getContent()
        );
    }*/

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void borrowingStatus(){
        LocalDate currentDate = LocalDate.now();
        for(Borrowing borrowing : borrowingRepository.findByStatus(BorrowingStatus.BORROWING)){
            if (borrowing.getReturnedDate() != null && currentDate.isAfter(borrowing.getBorrowDate().plusMonths(1))) {
                borrowing.setStatus(BorrowingStatus.OVERDUE);
                borrowingRepository.save(borrowing);
            }
        }
    }

    /*private boolean checkDuplicate(Borrowing borrowing) {
        List<Borrowing> borrowingList = borrowingRepository.findByStatusBorrowingOrDue();
        for(Borrowing b : borrowingList){
                if(borrowing.getBook().equals(b.getBook()) && borrowing.getUser().equals(b.getUser())) {
                    return true;
                }
            }
        return false;
    }*/



    public BorrowingResponse convertBorrowingToDTO(Borrowing borrowing) {
        BorrowingResponse borrowingDTO = new BorrowingResponse();
        borrowingDTO.setBorrowingId(borrowing.getId());
        borrowingDTO.setBorrowingDate(borrowing.getBorrowDate());
        borrowingDTO.setReturnDate(borrowing.getReturnedDate());
        borrowingDTO.setUsername(borrowing.getUser().getUserName());
        borrowingDTO.setBookName(borrowing.getBook().getBookName());
        borrowingDTO.setStatus(borrowing.getStatus().toString());
        return borrowingDTO;
    }

     /* @Override
    public void createBorrowingWorkbook(HttpServletResponse response) throws IOException {
        List<Book> books = borrowingRepository.findCurrentBorrowingBooks();
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet(messageConfig.getMessage(MessageError.BORROWING_TITLE_EXCEL));
        Row header = sheet.createRow(0); //excel is zero-based
        header.createCell(0).setCellValue("STT");
        header.createCell(1).setCellValue("Tên sách");
        header.createCell(2).setCellValue("Tác giả");
        int rowNum = 1; int x = 1;
        for(Book book : books){
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(x++);
            row.createCell(1).setCellValue(book.getBookName());
            row.createCell(2).setCellValue(book.getAuthor());
        }
        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
    }*/

}
