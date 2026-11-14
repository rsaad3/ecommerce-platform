//package com.ecommerce.inventory.exception;
//
//import jakarta.servlet.http.HttpServletRequest;
//import org.springframework.dao.DataIntegrityViolationException;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.orm.ObjectOptimisticLockingFailureException;
//import org.springframework.web.bind.MethodArgumentNotValidException;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.RestControllerAdvice;
//
//import java.time.LocalDateTime;
//import java.util.stream.Collectors;
//
//@RestControllerAdvice
//public class GlobalExceptionHandler {
//
//    @ExceptionHandler(ResourceNotFoundException.class)
//    public ResponseEntity<ApiError> handleNotFound(
//            ResourceNotFoundException ex,
//            HttpServletRequest request) {
//
//        return buildResponse(
//                HttpStatus.NOT_FOUND,
//                ex.getMessage(),
//                request
//        );
//    }
//
//    @ExceptionHandler({
//            InsufficientStockException.class,
//            ConflictException.class,
//            ObjectOptimisticLockingFailureException.class,
//            DataIntegrityViolationException.class
//    })
//    public ResponseEntity<ApiError> handleConflict(
//            Exception ex,
//            HttpServletRequest request) {
//
//        return buildResponse(
//                HttpStatus.CONFLICT,
//                ex.getMessage() != null
//                        ? ex.getMessage()
//                        : "Inventory conflict",
//                request
//        );
//    }
//
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public ResponseEntity<ApiError> handleValidation(
//            MethodArgumentNotValidException ex,
//            HttpServletRequest request) {
//
//        String message = ex.getBindingResult()
//                .getFieldErrors()
//                .stream()
//                .map(error ->
//                        error.getField() + ": "
//                                + error.getDefaultMessage()
//                )
//                .collect(Collectors.joining("; "));
//
//        return buildResponse(
//                HttpStatus.BAD_REQUEST,
//                message,
//                request
//        );
//    }
//
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<ApiError> handleGeneral(
//            Exception ex,
//            HttpServletRequest request) {
//
//        return buildResponse(
//                HttpStatus.INTERNAL_SERVER_ERROR,
//                "Unexpected server error",
//                request
//        );
//    }
//
//    private ResponseEntity<ApiError> buildResponse(
//            HttpStatus status,
//            String message,
//            HttpServletRequest request) {
//
//        ApiError error = new ApiError(
//                LocalDateTime.now(),
//                status.value(),
//                status.getReasonPhrase(),
//                message,
//                request.getRequestURI()
//        );
//
//        return ResponseEntity
//                .status(status)
//                .body(error);
//    }
//}