package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.dto.UserDto;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserTest extends ModelValidationTest<UserDto> {

    private UserDto dto;

    @BeforeEach
    public void initDto() {
        dto = new UserDto();
        dto.setName("John");
        dto.setEmail("john@example.com");
    }

    @Nested
    class NameTest {

        @Test
        public void shouldNotFindViolationWhenNameIsNullOnUpdate() {
            dto.setName(null);
            assertTrue(isModelValidForUpdate(dto));
        }

        @Test
        public void shouldFindViolationWhenNameIsNull() {
            dto.setName(null);
            assertFalse(isModelValidForCreate(dto));
        }

        @Test
        public void shouldFindViolationWhenNameIsEmpty() {
            dto.setName("");
            assertFalse(isModelValidForCreate(dto));
            assertFalse(isModelValidForUpdate(dto));
        }

        @Test
        public void shouldFindViolationWhenNameIsBlank() {
            dto.setName(" ");
            assertFalse(isModelValidForCreate(dto));
            assertTrue(isModelValidForUpdate(dto));
        }
    }

    @Nested
    class EmailTest {

        @Test
        public void shouldFinaViolationWhenEmailIsInvalidFormat() {
            dto.setEmail(" asdas d @@ asd");
            assertFalse(isModelValidForCreate(dto));
            assertFalse(isModelValidForUpdate(dto));
        }

        @Test
        public void shouldFinaViolationWhenEmailNullOnCreate() {
            dto.setEmail(null);
            assertFalse(isModelValidForCreate(dto));
        }

        @Test
        public void shouldNotFinaViolationWhenEmailNullOnUpdate() {
            dto.setEmail(null);
            assertTrue(isModelValidForUpdate(dto));
        }
    }
}
