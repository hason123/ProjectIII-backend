package com.example.projectiii.service.impl;

import com.example.projectiii.config.MessageConfig;
import com.example.projectiii.constant.BorrowingStatus;
import com.example.projectiii.constant.MessageError;
import com.example.projectiii.constant.RoleType;
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
import com.example.projectiii.service.NotificationService;
import com.example.projectiii.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor // Tự động inject các final field
public class BorrowingServiceImpl implements BorrowingService {

    private final BorrowingRepository borrowingRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final UserService userService; // Inject UserService
    private final NotificationService notificationService; // Inject NotificationService
    private final MessageConfig messageConfig;

    // --- 1. SINH VIÊN GỬI YÊU CẦU MƯỢN ---
    @Transactional
    @Override
    public BorrowingResponse requestBorrowing(Integer bookId) {
        User currentUser = userService.getCurrentUser();

        Book book = bookRepository.findById(bookId).orElseThrow(() ->
                new ResourceNotFoundException(messageConfig.getMessage(MessageError.BOOK_NOT_FOUND, bookId)));

        if (book.getQuantity() <= 0) {
            throw new BusinessException("Hiện tại đã hết sách! Vui lòng thử lại trong tương lai!");
        }

        // Kiểm tra xem sinh viên có đang giữ cuốn này mà chưa trả không (tránh spam)
        boolean alreadyBorrowing = borrowingRepository.existsByUserIdAndBook_BookIdAndStatus(
                currentUser.getId(), book.getBookId(), BorrowingStatus.BORROWING);
        if (alreadyBorrowing) {
            throw new BusinessException("Bạn đang mượn cuốn sách này rồi!");
        }

        // Tạo yêu cầu mượn
        Borrowing borrowing = new Borrowing();
        borrowing.setUser(currentUser);
        borrowing.setBook(book);
        borrowing.setBorrowDate(LocalDate.now());
        borrowing.setStatus(BorrowingStatus.PENDING); // Trạng thái chờ duyệt
        borrowing.setRenewCount(0);
        // Giữ chỗ sách (Trừ số lượng kho ngay lập tức)
        book.setQuantity(book.getQuantity() - 1);
        bookRepository.save(book);
        borrowingRepository.save(borrowing);
        // Gửi thông báo cho TẤT CẢ Thủ thư (LIBRARIAN)
        List<User> librarians = userRepository.findByRole_RoleName(RoleType.LIBRARIAN);
        String message = "Người dùng " + currentUser.getFullName()  + " muốn mượn sách: " + book.getBookName();
        for (User librarian : librarians) {
            notificationService.createNotification(
                    librarian,
                    "Yêu cầu mượn sách mới",
                    message,
                    "MƯỢN SÁCH",
                    null,
                    null
            );
        }

        return convertBorrowingToDTO(borrowing);
    }

    // --- 2. THỦ THƯ DUYỆT YÊU CẦU ---
    @Transactional
    @Override
    public BorrowingResponse approveBorrowing(BorrowingRequest request) {
        // Có thể check quyền Librarian ở đây hoặc ở Controller (@PreAuthorize)
        User currentUser = userService.getCurrentUser();
        User student = userRepository.findById(request.getUserId()).orElseThrow(() ->
                new ResourceNotFoundException("Không tìm tháy người dùng!"));
        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khóa học!"));
        Borrowing borrowing = borrowingRepository.findByUserIdAndBook_BookIdAndStatus(
                request.getUserId(),request.getBookId(), BorrowingStatus.PENDING);
        if (borrowing == null) {
            throw new ResourceNotFoundException("Yêu cầu mượn không tồn tại hoặc đã được xử lý!");
        }
        borrowing.setStatus(BorrowingStatus.BORROWING);
        borrowing.setUser(student);
        borrowing.setBook(book);
        borrowing.setBorrowDate(LocalDate.now());
        borrowingRepository.save(borrowing);

        String message = "Yêu cầu mượn sách " + book.getBookName() + " của bạn đã được chấp nhận!";
        notificationService.createNotification(student, "Yêu cầu được chấp thuận", message, "BORROWING_APPROVAL", null, null);
        return convertBorrowingToDTO(borrowing);
    }

    // --- 3. THỦ THƯ TỪ CHỐI YÊU CẦU ---
    @Transactional
    @Override
    public void rejectBorrowing(BorrowingRequest request) {
        User currentUser = userService.getCurrentUser();
        User student = userRepository.findById(request.getUserId()).orElseThrow(() ->
                new ResourceNotFoundException("Không tìm tháy người dùng!"));
        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khóa học!"));
        Borrowing borrowing = borrowingRepository.findByUserIdAndBook_BookIdAndStatus(
                request.getUserId(),request.getBookId(), BorrowingStatus.PENDING);
        if (borrowing == null) {
            throw new ResourceNotFoundException("Yêu cầu mượn không tồn tại hoặc đã được xử lý!");
        }
        book.setQuantity(book.getQuantity() + 1);
        borrowingRepository.delete(borrowing);
        String message = "Yêu cầu mượn sách " + book.getBookName() + " của bạn đã bị từ chối!";
        notificationService.createNotification(student, "Yêu cầu bị từ chối", message, "BORROWING_REJECTED", null, null);

    }

    // --- 4. TỰ ĐỘNG QUÉT SÁCH QUÁ HẠN (30 ngày) ---
    @Scheduled(cron = "0 0 8 * * *")
    @Transactional
    @Override
    public void scanOverdueBorrowings() {
        log.info("Scanning for overdue borrowings...");
        LocalDate today = LocalDate.now();

        // Lấy tất cả sách đang mượn
        List<Borrowing> activeBorrowings = borrowingRepository.findByStatus(BorrowingStatus.BORROWING);

        for (Borrowing borrowing : activeBorrowings) {
            // Logic: Nếu ngày hiện tại > ngày mượn + 30 ngày (hoặc check theo dueDate nếu đã lưu)
            // Ở đây dùng borrowDate + 30 ngày cho chắc chắn
            LocalDate dueDate = borrowing.getBorrowDate().plusDays(30);

            if (today.isAfter(dueDate)) {
                // Chuyển trạng thái sang quá hạn
                borrowing.setStatus(BorrowingStatus.OVERDUE);
                borrowingRepository.save(borrowing);
                // Gửi thông báo cho Sinh viên
                String message = "Sách '" + borrowing.getBook().getBookName() + "' đã QUÁ HẠN trả (Hạn chót: " + dueDate + "). Vui lòng trả sách ngay để tránh bị phạt.";
                notificationService.createNotification(
                        borrowing.getUser(),
                        "Thông báo quá hạn sách",
                        message,
                        "BORROWING_OVERDUE",
                        null,
                        null
                );

            }
        }
    }

    // Helper convert DTO
    private BorrowingResponse convertBorrowingToDTO(Borrowing borrowing) {
        BorrowingResponse response = new BorrowingResponse();
        response.setBorrowingId(borrowing.getId());
        response.setUserId(borrowing.getUser().getId());
        response.setUsername(borrowing.getUser().getUserName());
        response.setBookId(borrowing.getBook().getBookId());
        response.setBookName(borrowing.getBook().getBookName());
        response.setBorrowingDate(borrowing.getBorrowDate());
        response.setFullName(borrowing.getUser().getFullName());
        // Tính toán DueDate hiển thị (30 ngày từ ngày mượn)
        if (borrowing.getBorrowDate() != null) {
            response.setBorrowingDate(borrowing.getBorrowDate().plusDays(30));
        }
        response.setReturnDate(borrowing.getReturnedDate());
        response.setStatus(borrowing.getStatus().toString());
        return response;
    }

    @Override
    public BorrowingResponse getBorrowingById(Integer id) {
        log.info("Getting borrowing by id {}", id);
        Borrowing borrowing = borrowingRepository.findById(id).orElseThrow(() ->
        {
            log.error(messageConfig.getMessage(MessageError.BORROWING_NOT_FOUND, id));
            return new ResourceNotFoundException(messageConfig.getMessage(MessageError.BORROWING_NOT_FOUND, id));
        });
        return convertBorrowingToDTO(borrowing);
    }

    @Override
    public void deleteBorrowingById(Integer id) {
        Borrowing borrowing = borrowingRepository.findById(id).orElseThrow(() ->
        {
            log.error(messageConfig.getMessage(MessageError.BORROWING_NOT_FOUND, id));
            return new ResourceNotFoundException(messageConfig.getMessage(MessageError.BORROWING_NOT_FOUND, id));
        });
        borrowingRepository.delete(borrowing);
    }

    @Override
    public BorrowingResponse updateBorrowing(Integer id, BorrowingRequest request) {
        return null;
    }

    @Override
    public PageResponse<BorrowingResponse> getBorrowingPage(Pageable pageable) {
        Page<Borrowing> borrowingPage = borrowingRepository.findAll(pageable);
        Page<BorrowingResponse> borrowingResponseDTO = borrowingPage.map(this::convertBorrowingToDTO);
        return new PageResponse<>(
                borrowingResponseDTO.getNumber() + 1,
                borrowingResponseDTO.getNumberOfElements(),
                borrowingResponseDTO.getTotalPages(),
                borrowingResponseDTO.getContent()
        );
    }

    // ... Các hàm update, delete khác giữ nguyên hoặc chỉnh sửa tùy nhu cầu
}