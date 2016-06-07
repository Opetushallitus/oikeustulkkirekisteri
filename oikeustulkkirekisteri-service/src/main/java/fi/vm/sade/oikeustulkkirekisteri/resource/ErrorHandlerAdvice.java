package fi.vm.sade.oikeustulkkirekisteri.resource;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterators;
import fi.vm.sade.generic.common.ValidationException;
import fi.vm.sade.generic.service.exception.NotAuthorizedException;
import fi.vm.sade.oikeustulkkirekisteri.service.exception.NotFoundException;
import org.hibernate.annotations.common.util.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.io.Serializable;
import java.util.*;

/**
 * User: tommiratamaa
 * Date: 15.1.2016
 * Time: 15.57
 */
@ControllerAdvice(basePackageClasses = ErrorHandlerAdvice.class)
public class ErrorHandlerAdvice {
    public static final Locale FI = new Locale("fi", "FI");
    private static final Logger logger = LoggerFactory.getLogger(ErrorHandlerAdvice.class);

    @Autowired
    private MessageSource messageSource;

    @Autowired
    protected Validator validator;

    @InitBinder
    public void dataBinding(WebDataBinder binder) {
        binder.addValidators(new org.springframework.validation.Validator() {
            @Override
            public boolean supports(Class<?> clazz) {
                return true;
            }

            @Override
            public void validate(Object target, Errors errors) {
                Set<ConstraintViolation<Object>> violations = validator.validate(target);
                if (!violations.isEmpty()) {
                    throw new ConstraintViolationException(violations);
                }
            }
        });
        binder.setIgnoreUnknownFields(true);
    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND) // 404 Entity not found by primary key.
    @ExceptionHandler(NotFoundException.class) @ResponseBody
    public Map<String,Object> notFound(HttpServletRequest req, NotFoundException exception) {
        return handleException(req, exception, "error_NotFoundException",
                !Objects.equals(exception.getMessage(), "") ?
                        exception.getMessage() :
                        messageSource.getMessage("error_NotFoundException", new Object[0], getLocale(req)));
    }

    @ResponseStatus(value = HttpStatus.UNAUTHORIZED) // 401 Not authorized
    @ExceptionHandler(NotAuthorizedException.class) @ResponseBody
    public Map<String,Object> notAuthorized(HttpServletRequest req, NotAuthorizedException exception) {
        return handleException(req, exception, "error_NotAuthorizedException",
                messageSource.getMessage("error_NotAuthorizedException", new Object[0], getLocale(req)));
    }

    private Locale getLocale(HttpServletRequest req) {
        Locale locale;
        if (req.getParameter("lang") != null) {
            locale = Locale.forLanguageTag(req.getParameter("lang"));
        } else if (req.getParameter("locale") != null) {
            locale = Locale.forLanguageTag(req.getParameter("locale"));
        } else {
            locale = FI;
        }
        return locale;
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST) // 400 Bad request.
    @ExceptionHandler(org.hibernate.exception.ConstraintViolationException.class) @ResponseBody
    public Map<String,Object> badConstraintViolatingRequest(HttpServletRequest req, ConstraintViolationException exception) {
        return handleConstraintViolations(req, exception, exception.getConstraintViolations());
    }
    
    @ResponseStatus(value = HttpStatus.BAD_REQUEST) // 400 Bad request.
    @ExceptionHandler(ConstraintViolationException.class) @ResponseBody
    public Map<String,Object> badRequest(HttpServletRequest req, ConstraintViolationException exception) {
        return handleConstraintViolations(req, exception, exception.getConstraintViolations());
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST) // 400 Bad request.
    @ExceptionHandler(ValidationException.class) @ResponseBody
    public Map<String,Object> badRequest(HttpServletRequest req, ValidationException exception) {
        Collection<ViolationDto> violations = Collections2.transform(exception.getViolations(), VIOLATIONS_TRANSFORMER);
        Collection<String> violationsMsgs = exception.getValidationMessages();
        Map<String,Object> result = handleException(req, exception, exception.getKey(),
                StringHelper.join(", ", violationsMsgs.iterator()));
        result.put("errors", violationsMsgs);
        result.put("violations", violations);
        return result;
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST) // 400 Bad Request
    @ExceptionHandler(java.lang.IllegalArgumentException.class) @ResponseBody
    public Map<String,Object> badRequest(HttpServletRequest req, java.lang.IllegalArgumentException exception) {
        return handleException(req, exception, "bad_request_illegal_argumentex", exception.getMessage());
    }
    
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR) // 500 Internal
    @ExceptionHandler(java.lang.Exception.class) @ResponseBody // any other type
    public Map<String,Object> internalError(HttpServletRequest req, java.lang.Exception exception) {
        return handleException(req, exception, "internal_server_error", exception.getMessage());
    }
    
    private Map<String,Object> handleConstraintViolations(HttpServletRequest req, Exception exception,
                                                          Set<? extends ConstraintViolation<?>> exViolations) {
        Collection<ViolationDto> violations = Collections2.transform(exViolations, VIOLATIONS_TRANSFORMER);
        Collection<String> violationsMsgs = Collections2.transform(exViolations, MESSAGES_TRANSFORMER);
        Map<String,Object> result = handleException(req, exception, "bad_request_error",
                StringHelper.join(", ", violationsMsgs.iterator()));
        result.put("errors", violationsMsgs);
        result.put("violations", violations);
        return result;
    }

    protected Map<String,Object> handleException(HttpServletRequest req, Throwable exception, String messageKey, String message,
                                                 Object... params) {
        logger.error("Request: " + req.getRequestURL() + " raised " + exception, exception);
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("message", message);
        result.put("messageKey", messageKey);
        result.put("messageParams", params);
        result.put("errorType", exception.getClass().getSimpleName());
        result.put("url", req.getRequestURL());
        result.put("method", req.getMethod());
        result.put("parameters", req.getParameterMap());
        return result;
    }

    private static final Function<? super ConstraintViolation<?>,String> MESSAGES_TRANSFORMER = violation -> {
        if (violation == null) {
            return null;
        }
        return violation.getMessage() + ": " + violation.getInvalidValue();
    };
    private static final Function<? super ConstraintViolation<?>,ViolationDto> VIOLATIONS_TRANSFORMER
            = violation -> {
        if (violation == null) {
            return null;
        }
        return new ViolationDto(violation.getPropertyPath().toString(),
                Iterators.getLast(violation.getPropertyPath().iterator()).toString(),
                violation.getMessage(),
                violation.getInvalidValue());
    };
    
    public static class ViolationDto implements Serializable {
        private String field;
        private String path;
        private String message;
        private Object value;

        public ViolationDto() {
        }

        public ViolationDto(String path, String field, String message, Object value) {
            this.path = path;
            this.field = field;
            this.message = message;
            this.value = value;
        }

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }
    }
}
