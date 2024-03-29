package org.project.nuwabackend.global.exception.custom;

import org.project.nuwabackend.global.response.type.ErrorMessage;

public class NotFoundException extends RuntimeException {
    public NotFoundException(ErrorMessage message) {
        super(message.getMessage());
    }
}
