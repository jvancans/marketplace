package io.marketplace.api.config;

import io.marketplace.api.error.CustomValidationFeature;
import io.marketplace.api.trade.TradeResource;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

import static org.glassfish.jersey.server.ServerProperties.BV_FEATURE_DISABLE;

@Component
public class JerseyConfig extends ResourceConfig {
    public JerseyConfig() {
        // end points
        register(TradeResource.class);
        //features
        register(CustomValidationFeature.class);
        //properties
        property(BV_FEATURE_DISABLE, true);
    }
}