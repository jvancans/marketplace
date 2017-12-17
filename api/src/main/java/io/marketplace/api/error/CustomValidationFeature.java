package io.marketplace.api.error;

import org.glassfish.jersey.server.validation.internal.ValidationBinder;

import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;

public final class CustomValidationFeature implements Feature {
    @Override
    public boolean configure(FeatureContext context) {
        context.register(new ValidationBinder());
        context.register(CustomValidationExceptionMapper.class);
        return true;
    }
}
