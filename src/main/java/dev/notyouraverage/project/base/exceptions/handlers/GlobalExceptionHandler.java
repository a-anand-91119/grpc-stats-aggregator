package dev.notyouraverage.project.base.exceptions.handlers;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import dev.notyouraverage.project.base.constants.Constants;
import dev.notyouraverage.project.base.constants.MessageConstants;
import dev.notyouraverage.project.base.dtos.response.wrapper.ErrorResponse;
import dev.notyouraverage.project.base.dtos.response.wrapper.ResponseWrapper;
import dev.notyouraverage.project.base.enums.ErrorCode;
import dev.notyouraverage.project.base.exceptions.RestException;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(value = { RestException.class })
    public ResponseEntity<ResponseWrapper<Void>> handleResponseException(
            RestException restException
    ) {
        return buildErrorResponse(restException.getHttpStatus(), restException.getErrorResponses());
    }

    @ExceptionHandler(value = { Throwable.class })
    public ResponseEntity<ResponseWrapper<Void>> handleGenericThrowable(Throwable throwable) {
        log.error(
                String.format(
                        MessageConstants.GLOBAL_EXCEPTION_HANDLER_CAPTURE_MESSAGE,
                        throwable.getClass().getName()
                ),
                throwable
        );
        ErrorResponse errorResponse = ErrorResponse.from(ErrorCode.INTERNAL_SERVER_ERROR);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, List.of(errorResponse));
    }

    @ExceptionHandler(value = { MissingServletRequestParameterException.class })
    public ResponseEntity<ResponseWrapper<Void>> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException exception
    ) {
        ErrorResponse errorResponse = ErrorResponse
                .from(ErrorCode.INPUT_VALIDATION_ERROR, exception.getBody().getDetail());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, Collections.singletonList(errorResponse));
    }

    @ExceptionHandler(value = { MethodArgumentNotValidException.class })
    public ResponseEntity<ResponseWrapper<Void>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException exception
    ) {
        List<ErrorResponse> errorResponses = exception.getBindingResult()
                .getAllErrors()
                .stream()
                .map(objectError -> {
                    String errorDetail;
                    if (objectError instanceof FieldError fieldError) {
                        errorDetail = String.format(
                                MessageConstants.FIELD_VALIDATION_ERROR_MESSAGE_FORMAT,
                                fieldError.getField(),
                                fieldError.getDefaultMessage()
                        );
                    } else {
                        errorDetail = objectError.getDefaultMessage();
                    }
                    return ErrorResponse.from(ErrorCode.INPUT_VALIDATION_ERROR, errorDetail);
                })
                .toList();
        return buildErrorResponse(HttpStatus.BAD_REQUEST, errorResponses);
    }

    @ExceptionHandler(value = { ConstraintViolationException.class })
    public ResponseEntity<ResponseWrapper<Void>> handleConstraintViolationException(
            ConstraintViolationException exception
    ) {
        List<ErrorResponse> errorResponses = exception.getConstraintViolations()
                .stream()
                .map(constraintViolation -> {
                    Path propertyPath = constraintViolation.getPropertyPath();
                    String leafNodeName = null;
                    for (Path.Node node : propertyPath) {
                        leafNodeName = node.getName();
                    }
                    return ErrorResponse.from(
                            ErrorCode.INPUT_VALIDATION_ERROR,
                            String.format(
                                    MessageConstants.FIELD_VALIDATION_ERROR_MESSAGE_FORMAT,
                                    leafNodeName,
                                    constraintViolation.getMessage()
                            )
                    );
                })
                .toList();
        return buildErrorResponse(HttpStatus.BAD_REQUEST, errorResponses);
    }

    @ExceptionHandler(value = { HttpMessageNotReadableException.class })
    public ResponseEntity<ResponseWrapper<Void>> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException httpMessageNotReadableException
    ) {
        ArrayList<ErrorResponse> errorResponses = new ArrayList<>();
        if (httpMessageNotReadableException.getCause() instanceof InvalidFormatException invalidFormatException) {
            errorResponses.add(handleInvalidFormatException(invalidFormatException));
        } else if (httpMessageNotReadableException.getCause() instanceof JsonMappingException) {
            errorResponses.add(
                    ErrorResponse
                            .from(ErrorCode.INPUT_VALIDATION_ERROR, MessageConstants.ERROR_PARSING_THE_REQUEST_BODY)
            );
        } else if (
            httpMessageNotReadableException.getMessage()
                    .startsWith(MessageConstants.REQUIRED_REQUEST_BODY_IS_MISSING)
        ) {
            errorResponses.add(
                    ErrorResponse
                            .from(ErrorCode.INPUT_VALIDATION_ERROR, MessageConstants.REQUIRED_REQUEST_BODY_IS_MISSING)
            );
        }
        return buildErrorResponse(HttpStatus.BAD_REQUEST, errorResponses);
    }

    @ExceptionHandler(value = { NoHandlerFoundException.class })
    public ResponseEntity<ResponseWrapper<Void>> handleNoHandlerFoundException(
            NoHandlerFoundException exception
    ) {
        ErrorResponse errorResponse = ErrorResponse.from(
                ErrorCode.NO_HANDLER_FOUND,
                String.format(
                        MessageConstants.NO_HANDLER_FOUND_FOR_THE_REQUESTED_PATH,
                        exception.getRequestURL()
                )
        );
        return buildErrorResponse(
                HttpStatus.NOT_FOUND,
                List.of(errorResponse)
        );
    }

    @ExceptionHandler(value = { HttpMediaTypeNotSupportedException.class })
    public ResponseEntity<ResponseWrapper<Void>> handleHttpMediaTypeNotSupportedException(
            HttpMediaTypeNotSupportedException exception
    ) {
        ErrorResponse errorResponse = ErrorResponse.from(
                ErrorCode.INPUT_VALIDATION_ERROR,
                String.format(
                        MessageConstants.UNSUPPORTED_MEDIA_TYPE_MESSAGE,
                        exception.getContentType(),
                        exception.getSupportedMediaTypes()
                )
        );
        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                List.of(errorResponse)
        );
    }

    private ErrorResponse handleInvalidFormatException(
            InvalidFormatException invalidFormatException
    ) {
        String errorDetail;
        Class<?> targetType = invalidFormatException.getTargetType();
        if (targetType == null) {
            errorDetail = MessageConstants.INVALID_FORMAT;
        } else if (targetType.isEnum()) {
            List<JsonMappingException.Reference> path = invalidFormatException.getPath();

            JsonMappingException.Reference errorPath = path.getLast()
                    .getDescription()
                    .equals("java.lang.Object[][0]") ? path.get(path.size() - 2) : path.getLast();
            String fieldName = errorPath.getFieldName();
            errorDetail = String.format(
                    MessageConstants.ENUM_INVALID_CAST_MESSAGE,
                    fieldName,
                    Arrays.toString(targetType.getEnumConstants())
            );
        } else if (targetType.isAssignableFrom(LocalDateTime.class)) {
            String path = invalidFormatException.getPath()
                    .stream()
                    .map(JsonMappingException.Reference::getFieldName)
                    .collect(Collectors.joining("."));
            errorDetail = String.format(
                    MessageConstants.TIMESTAMP_MUST_BE_OF_FORMAT,
                    path,
                    Constants.TIMESTAMP_FORMAT.replace("'", "")
            );
        } else {
            String path = invalidFormatException.getPath()
                    .stream()
                    .map(JsonMappingException.Reference::getFieldName)
                    .collect(Collectors.joining("."));
            errorDetail = String.format(
                    MessageConstants.INVALID_FIELD_TYPE_ASSIGNMENT,
                    path,
                    invalidFormatException.getValue(),
                    invalidFormatException.getTargetType().getSimpleName()
            );
        }
        return ErrorResponse.from(ErrorCode.INPUT_VALIDATION_ERROR, errorDetail);
    }

    private ResponseEntity<ResponseWrapper<Void>> buildErrorResponse(
            HttpStatus httpStatus,
            List<ErrorResponse> errorResponses
    ) {
        return ResponseEntity.status(httpStatus).body(ResponseWrapper.failure(errorResponses));
    }
}
