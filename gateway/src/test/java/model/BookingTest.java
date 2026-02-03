package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.dto.BookingDto;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BookingTest extends ModelValidationTest<BookingDto> {
    private BookingDto dto;
    private final LocalDateTime start = LocalDateTime.of(2030, 1, 1, 0, 0);
    private final LocalDateTime end = LocalDateTime.of(2030, 1, 2, 0, 0);

    @BeforeEach
    public void initDto() {
        dto = new BookingDto();
        dto.setItemId(1L);
        dto.setStart(start);
        dto.setEnd(end);
    }

    @Test
    public void shouldNotFindViolation() {
        assertTrue(isModelValid(dto));
    }

    @Nested
    class IdTest {

        @Test
        public void shouldFindViolationWhenIdIsNull() {
            dto.setItemId(null);
            assertFalse(isModelValid(dto));
        }

        @Test
        public void shouldFindViolationWhenIdIsZero() {
            dto.setItemId(0L);
            assertFalse(isModelValid(dto));
        }

        @Test
        public void shouldFindViolationWhenIdIsNegative() {
            dto.setItemId(-1L);
            assertFalse(isModelValid(dto));
        }
    }

    @Nested
    class StartTest {

        @Test
        public void shouldFindViolationWhenStartInPast() {
            dto.setStart(start.minusYears(100));
            assertFalse(isModelValid(dto));
        }

        @Test
        public void shouldFindViolationWhenStartIsNull() {
            dto.setStart(null);
            assertFalse(isModelValid(dto));
        }

        @Test
        public void shouldNotFindViolationWhenFuture() {
            dto.setStart(start.plusYears(100));
            assertTrue(isModelValid(dto));
        }
    }

    @Nested
    class EndTest {

        @Test
        public void shouldFindViolationWhenEndInPast() {
            dto.setEnd(end.minusYears(100));
            assertFalse(isModelValid(dto));
        }

        @Test
        public void shouldFindViolationWhenEndIsNull() {
            dto.setEnd(null);
            assertFalse(isModelValid(dto));
        }

        @Test
        public void shouldNotFindViolationWhenFuture() {
            dto.setEnd(end.plusYears(100));
            assertTrue(isModelValid(dto));
        }
    }

}
