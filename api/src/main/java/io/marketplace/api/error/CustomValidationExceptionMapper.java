package io.marketplace.api.error;

import io.marketplace.api.error.model.ErrorResponse;
import io.marketplace.api.error.model.ErrorType;
import io.marketplace.api.error.model.ValidationError;
import io.marketplace.api.error.model.ValidationErrorResponse;
import org.glassfish.jersey.server.validation.internal.ValidationHelper;

import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import javax.ws.rs.core.*;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Provider
public class CustomValidationExceptionMapper implements ExceptionMapper<ValidationException> {

    @Context
    private javax.inject.Provider<Request> request;

    @Override
    public Response toResponse(ValidationException exception) {
        if (exception instanceof ConstraintViolationException) {
            final ConstraintViolationException cve = (ConstraintViolationException) exception;
            final Response.ResponseBuilder response = Response.status(ValidationHelper.getResponseStatus(cve));

            return response
                    .type(resolveMediaType(request))
                    .entity(new ValidationErrorResponse(mapErrors(cve)))
                    .build();
        } else {
            return Response
                    .serverError()
                    .entity(new ErrorResponse(ErrorType.INTERNAL_ERROR, exception.getMessage()))
                    .build();
        }
    }

    private static List<ValidationError> mapErrors(ConstraintViolationException cve) {
        return cve.getConstraintViolations()
                .stream()
                .map(violation -> new ValidationError(violation.getMessage()))
                .collect(toList());
    }

    private MediaType resolveMediaType(javax.inject.Provider<Request> request) {
        final List<Variant> variants = Variant.mediaTypes(
                MediaType.TEXT_PLAIN_TYPE,
                MediaType.TEXT_HTML_TYPE,
                MediaType.APPLICATION_XML_TYPE,
                MediaType.APPLICATION_JSON_TYPE).build();
        final Variant variant = request.get().selectVariant(variants);
        return variant != null ? variant.getMediaType() : MediaType.TEXT_PLAIN_TYPE;
    }
}