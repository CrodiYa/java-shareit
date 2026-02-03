package model;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import ru.practicum.shareit.OnCreate;
import ru.practicum.shareit.OnUpdate;

public class ModelValidationTest<T> {
    protected static Validator validator;

    @BeforeAll
    public static void setValidator() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    protected boolean isModelValid(T model) {
        return validator.validate(model).isEmpty();
    }

    protected boolean isModelValidForCreate(T model) {
        return validator.validate(model, OnCreate.class).isEmpty();
    }

    protected boolean isModelValidForUpdate(T model) {
        return validator.validate(model, OnUpdate.class).isEmpty();
    }
}
