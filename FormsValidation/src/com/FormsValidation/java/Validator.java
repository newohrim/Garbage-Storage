package com.FormsValidation.java;

import java.util.Set;

public interface Validator {
    Set<ValidationError> validate(Object object);
}
