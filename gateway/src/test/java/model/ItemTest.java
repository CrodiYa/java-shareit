package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.dto.ItemDto;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ItemTest extends ModelValidationTest<ItemDto> {

    private ItemDto dto;

    @BeforeEach
    public void initDto() {
        dto = new ItemDto();
        dto.setId(1L);
        dto.setName("John");
        dto.setDescription("John Description");
        dto.setAvailable(true);
        dto.setRequestId(1L);
    }

    @Test
    public void shouldNotFindViolation() {
        assertTrue(isModelValid(dto));
    }

    @Test
    public void shouldFindViolationWhenAvailableIsNullForCreate() {
        dto.setAvailable(null);
        assertFalse(isModelValidForCreate(dto));
    }

    @Test
    public void shouldNotFindViolationWhenAvailableIsNullForUpdate() {
        dto.setAvailable(null);
        assertTrue(isModelValidForUpdate(dto));
    }

    @Nested
    class IdTest {

        @Test
        public void shouldFindViolationWhenIdIsZero() {
            dto.setRequestId(0L);
            assertFalse(isModelValid(dto));
        }

        @Test
        public void shouldFindViolationWhenIdIsNegative() {
            dto.setRequestId(-1L);
            assertFalse(isModelValid(dto));
        }
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
    class DescriptionTest {

        @Test
        public void shouldNotFindViolationWhenDescriptionIsNullOnUpdate() {
            dto.setDescription(null);
            assertTrue(isModelValidForUpdate(dto));
        }

        @Test
        public void shouldFindViolationWhenDescriptionIsNull() {
            dto.setDescription(null);
            assertFalse(isModelValidForCreate(dto));
        }

        @Test
        public void shouldFindViolationWhenDescriptionIsEmpty() {
            dto.setDescription("");
            assertFalse(isModelValidForCreate(dto));
            assertFalse(isModelValidForUpdate(dto));
        }

        @Test
        public void shouldFindViolationWhenDescriptionIsBlank() {
            dto.setDescription(" ");
            assertFalse(isModelValidForCreate(dto));
            assertTrue(isModelValidForUpdate(dto));
        }
    }
}
