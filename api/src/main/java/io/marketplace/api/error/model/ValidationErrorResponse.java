package io.marketplace.api.error.model;


import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
public class ValidationErrorResponse extends ErrorResponse {
    private final List<ValidationError> errors;

    public ValidationErrorResponse(List<ValidationError> errors) {
        super(ErrorType.VALIDATION_ERROR, "One or more fields did not pass validation");
        this.errors = errors;
    }

    public List<ValidationError> getErrors() {
        return errors;
    }
}
