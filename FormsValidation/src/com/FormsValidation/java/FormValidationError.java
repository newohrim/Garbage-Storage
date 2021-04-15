package com.FormsValidation.java;

public class FormValidationError implements ValidationError {

    private final String message;
    private final String path;
    private final Object value;

    public FormValidationError(final String messageToUser, final String pathToValue, final Object unvalidatedValue)
    {
        message = messageToUser;
        path = pathToValue;
        value = unvalidatedValue;
    }

    public FormValidationError(final String messageToUser, final StringBuilder pathToValue, final Object unvalidatedValue)
    {
        message = messageToUser;
        path = pathToValue.toString();
        value = unvalidatedValue;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public Object getFailedValue() {
        return value;
    }
}
