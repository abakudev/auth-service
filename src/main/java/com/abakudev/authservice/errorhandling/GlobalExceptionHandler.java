package com.abakudev.authservice.errorhandling;

import com.abakudev.authservice.errorhandling.exceptions.BusinessException;
import com.abakudev.authservice.errorhandling.exceptions.BusinessExceptionReason;
import com.abakudev.authservice.errorhandling.response.ErrorResponse;
import com.abakudev.authservice.errorhandling.response.InvalidParameter;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import static java.lang.String.format;
import static java.util.Collections.singletonList;

/**
 * Global API exception handler responsible for catching any uncaught {@link Exception} and converting it into a prettier
 * JSON response.
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {


    /**
     * Handles the uncaught {@link Exception} exceptions and returns a JSON formatted response.
     *
     * @param ex      the ex
     * @param request the request on which the ex occurred
     * @return a JSON formatted response containing the ex details and additional fields
     */
    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<Object> handleUncaughtException(final Exception ex, final ServletWebRequest request) {
        log(ex, request);
        final var errorResponse = new ErrorResponse(Exception.class.getSimpleName(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                HttpStatus.INTERNAL_SERVER_ERROR);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    /**
     * Handles the uncaught {@link BusinessException} exceptions and returns a JSON formatted response.
     *
     * @param ex      the ex
     * @param request the request on which the ex occurred
     * @return a JSON formatted response containing the ex details and additional fields
     */
    @ExceptionHandler({BusinessException.class})
    public ResponseEntity<Object> handleCustomBusinessException(final BusinessException ex,
                                                                final ServletWebRequest request) {
        log(ex, request);
        final ErrorResponse errorResponseDto = new ErrorResponse(ex.getCode(), ex.getMessage(), ex.getHttpStatus());
        return ResponseEntity.status(ex.getHttpStatus()).body(errorResponseDto);
    }

    /**
     * Handles the uncaught {@link AccessDeniedException} exceptions and returns a JSON formatted response.
     *
     * @param ex      the ex
     * @param request the request on which the ex occurred
     * @return a JSON formatted response containing the ex details and additional fields
     */
    @ExceptionHandler({AccessDeniedException.class})
    public ResponseEntity<Object> handleAccessDeniedException(final AccessDeniedException ex,
                                                              final ServletWebRequest request) {
        log(ex, request);
        final ErrorResponse errorResponse = new ErrorResponse(BusinessExceptionReason.ACCESS_DENIED);
        return ResponseEntity.status(BusinessExceptionReason.ACCESS_DENIED.getHttpStatus()).body(errorResponse);
    }

    /**
     * Handles the uncaught {@link ConstraintViolationException} exceptions and returns a JSON formatted response.
     *
     * @param ex      the ex
     * @param request the request on which the ex occurred
     * @return a JSON formatted response containing the ex details and additional fields
     */
    @ExceptionHandler(value = {ConstraintViolationException.class})
    public ResponseEntity<Object> handleConstraintViolationException(final ConstraintViolationException ex,
                                                                     final ServletWebRequest request) {
        log(ex, request);

        final List<InvalidParameter> invalidParameters = new ArrayList<>();
        ex.getConstraintViolations().forEach(constraintViolation -> {
            final Iterator<Path.Node> it = constraintViolation.getPropertyPath().iterator();
            if (it.hasNext()) {
                try {
                    it.next();
                    final Path.Node n = it.next();
                    final InvalidParameter invalidParameter = new InvalidParameter(n.getName(),
                            constraintViolation.getMessage());
                    invalidParameters.add(invalidParameter);
                } catch (final Exception e) {
                    log.warn("[Advocatus] Can't extract the information about constraint violation");
                }
            }
        });

        final ErrorResponse errorResponseDto = new ErrorResponse(ConstraintViolationException.class.getSimpleName(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(), HttpStatus.BAD_REQUEST, invalidParameters);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponseDto);
    }

    /**
     * Handles the uncaught {@link HttpMessageNotReadableException} exceptions and returns a JSON formatted response.
     *
     * @param ex      the ex
     * @param request the request on which the ex occurred
     * @return a JSON formatted response containing the ex details and additional fields
     */
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(final HttpMessageNotReadableException ex,
                                                                  final HttpHeaders headers,
                                                                  final HttpStatusCode status,
                                                                  final WebRequest request) {
        log(ex, (ServletWebRequest) request);
        final ErrorResponse errorResponse = new ErrorResponse(HttpMessageNotReadableException.class.getSimpleName(),
                ex.getMessage(),
                HttpStatus.BAD_REQUEST);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Handles the uncaught {@link HttpRequestMethodNotSupportedException} exceptions and returns a JSON formatted
     * response.
     *
     * @param ex      the ex
     * @param request the request on which the ex occurred
     * @return a JSON formatted response containing the ex details and additional fields
     */
    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
            final HttpRequestMethodNotSupportedException ex, final HttpHeaders headers, final HttpStatusCode status,
            final WebRequest request) {
        log(ex, (ServletWebRequest) request);
        final ErrorResponse errorResponse =
                new ErrorResponse(HttpRequestMethodNotSupportedException.class.getSimpleName(), ex.getMessage(),
                        HttpStatus.METHOD_NOT_ALLOWED);
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(errorResponse);
    }

    /**
     * Handles the uncaught {@link MethodArgumentNotValidException} exceptions and returns a JSON formatted response.
     *
     * @param ex      the ex
     * @param request the request on which the ex occurred
     * @return a JSON formatted response containing the ex details and additional fields
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(final MethodArgumentNotValidException ex,
                                                                  final HttpHeaders headers,
                                                                  final HttpStatusCode status,
                                                                  final WebRequest request) {
        log(ex, (ServletWebRequest) request);
        final List<InvalidParameter> invalidParameters = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> new InvalidParameter(fieldError.getField(), fieldError.getDefaultMessage()))
                .toList();

        final ErrorResponse errorResponse = new ErrorResponse(MethodArgumentNotValidException.class.getSimpleName(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(), HttpStatus.BAD_REQUEST, invalidParameters);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Handles the uncaught {@link ServletRequestBindingException} exceptions and returns a JSON formatted response.
     *
     * @param ex      the ex
     * @param request the request on which the ex occurred
     * @return a JSON formatted response containing the ex details and additional fields
     */
    @Override
    protected ResponseEntity<Object> handleServletRequestBindingException(final ServletRequestBindingException ex,
                                                                          final HttpHeaders headers,
                                                                          final HttpStatusCode status,
                                                                          final WebRequest request) {
        log(ex, (ServletWebRequest) request);

        final String missingParameter;
        final String missingParameterType;

        if (ex instanceof MissingRequestHeaderException) {
            missingParameter = ((MissingRequestHeaderException) ex).getHeaderName();
            missingParameterType = "header";
        } else if (ex instanceof MissingServletRequestParameterException) {
            missingParameter = ((MissingServletRequestParameterException) ex).getParameterName();
            missingParameterType = "query";
        } else if (ex instanceof MissingPathVariableException) {
            missingParameter = ((MissingPathVariableException) ex).getVariableName();
            missingParameterType = "path";
        } else {
            missingParameter = "unknown";
            missingParameterType = "unknown";
        }

        final InvalidParameter missingParameterDto = new InvalidParameter(missingParameter,
                format("Missing %s parameter with name '%s'", missingParameterType, missingParameter));

        final ErrorResponse errorResponse = new ErrorResponse(ServletRequestBindingException.class.getSimpleName(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(), HttpStatus.BAD_REQUEST, singletonList(missingParameterDto));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Handles the uncaught {@link TypeMismatchException} exceptions and returns a JSON formatted response.
     *
     * @param ex      the ex
     * @param request the request on which the ex occurred
     * @return a JSON formatted response containing the ex details and additional fields
     */
    @Override
    protected ResponseEntity<Object> handleTypeMismatch(final TypeMismatchException ex, final HttpHeaders headers,
                                                        final HttpStatusCode status, final WebRequest request) {
        log(ex, (ServletWebRequest) request);

        String parameter = ex.getPropertyName();
        if (ex instanceof MethodArgumentTypeMismatchException) {
            parameter = ((MethodArgumentTypeMismatchException) ex).getName();
        }

        final ErrorResponse errorResponse = new ErrorResponse(TypeMismatchException.class.getSimpleName(),
                format("Unexpected type specified for '%s' parameter. Required '%s'", parameter, ex.getRequiredType()),
                HttpStatus.BAD_REQUEST);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Handles the uncaught {@link MissingPathVariableException} exceptions and returns a JSON formatted response.
     *
     * @param ex      the ex
     * @param request the request on which the ex occurred
     * @return a JSON formatted response containing the ex details and additional fields
     */
    @Override
    protected ResponseEntity<Object> handleMissingPathVariable(final MissingPathVariableException ex,
                                                               final HttpHeaders headers,
                                                               final HttpStatusCode status,
                                                               final WebRequest request) {
        return handleServletRequestBindingException(ex, headers, status, request);
    }

    /**
     * Handles the uncaught {@link MissingServletRequestParameterException} exceptions and returns a JSON formatted
     * response.
     *
     * @param ex      the ex
     * @param request the request on which the ex occurred
     * @return a JSON formatted response containing the ex details and additional fields
     */
    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            final MissingServletRequestParameterException ex, final HttpHeaders headers, final HttpStatusCode status,
            final WebRequest request) {
        log(ex, (ServletWebRequest) request);
        return handleServletRequestBindingException(ex, headers, status, request);
    }


    private void log(final Exception ex, final ServletWebRequest request) {
        final Optional<HttpMethod> httpMethod;
        final Optional<String> requestUrl;

        final Optional<ServletWebRequest> possibleIncomingNullRequest = Optional.ofNullable(request);
        if (possibleIncomingNullRequest.isPresent()) {
            // get the HTTP Method
            httpMethod = Optional.of(possibleIncomingNullRequest.get().getHttpMethod());
            if (Optional.of(possibleIncomingNullRequest.get().getRequest()).isPresent()) {
                // get the Request URL
                requestUrl = Optional.of(possibleIncomingNullRequest.get().getRequest().getRequestURL().toString());
            } else {
                requestUrl = Optional.empty();
            }
        } else {
            httpMethod = Optional.empty();
            requestUrl = Optional.empty();
        }

        log.error("Request {} {} failed with exception reason: {}", (httpMethod.isPresent() ? httpMethod.get() : "'null'"),
                (requestUrl.orElse("'null'")), ex.getMessage(), ex);
    }
}
