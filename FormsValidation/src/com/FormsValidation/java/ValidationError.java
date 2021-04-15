package com.FormsValidation.java;

public interface ValidationError {
    String getMessage();
    String getPath();
    Object getFailedValue();
}
