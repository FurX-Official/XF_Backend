package com.xf.backend.common.exception;

public class ResourceNotFoundException extends BusinessException {

    public ResourceNotFoundException(String resource, String field, String value) {
        super("RESOURCE_NOT_FOUND", resource + " not found with " + field + ": " + value);
    }

    public ResourceNotFoundException(String message) {
        super("RESOURCE_NOT_FOUND", message);
    }
}
