package io.marketplace.api.error.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ErrorResponse {
    private final ErrorType type;
    private final String description;

    public ErrorResponse(ErrorType type, String description) {
        this.type = type;
        this.description = description;
    }

    public ErrorType getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }
}
