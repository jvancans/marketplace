package io.marketplace.api.error;

import io.marketplace.api.error.model.ValidationErrorResponse;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import javax.inject.Provider;
import javax.validation.*;
import javax.validation.constraints.NotNull;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Variant;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CustomValidationExceptionMapperTest {
    @Mock
    private Provider<Request> request;

    @InjectMocks
    private CustomValidationExceptionMapper exceptionMapper;

    @Before
    public void init() {
        Request requestMock = mock(Request.class);
        when(request.get()).thenReturn(requestMock);
        when(requestMock.selectVariant(anyListOf(Variant.class)))
                .thenReturn(new Variant(MediaType.APPLICATION_JSON_TYPE, "", ""));
    }

    @Test
    public void testInternalErrorResponseIsBuiltIfExceptionIsNotConstraintViolationException() {
        Response response = exceptionMapper.toResponse(new UnexpectedTypeException());
        assertThat(response, notNullValue());
        assertThat(response.getStatus(), is(HttpStatus.INTERNAL_SERVER_ERROR.value()));
    }

    @Test
    public void testValidationErrorResponseIsBuiltForConstraintViolationException() {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<TestBean>> validate = validator.validate(new TestBean());
        ConstraintViolationException exception = new ConstraintViolationException(validate);

        Response response = exceptionMapper.toResponse(exception);
        assertThat(response, notNullValue());
        assertThat(response.getStatus(), is(HttpStatus.BAD_REQUEST.value()));
        assertTrue(response.getEntity() instanceof ValidationErrorResponse);
        ValidationErrorResponse errorResponse = (ValidationErrorResponse) response.getEntity();
        assertThat(errorResponse.getErrors(), Matchers.not(empty()));
        assertThat(errorResponse.getErrors().size(), is(1));
        assertThat(errorResponse.getErrors().get(0).getMessage(), is("testValidationMessage"));
    }

    private class TestBean {
        @NotNull(message = "testValidationMessage")
        private String test;
    }
}
