package io.github.artus;

import io.github.artus.exceptions.Throwables;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static io.github.artus.exceptions.Throwables.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for simple Throwables.
 */
public class ThrowablesTest {

    List<Throwable> generateThrowableList(int size) {
        List<Throwable> causes = new ArrayList<>();
        if (size <= 0) return causes;
        Throwable currentThrowable = new Throwable();
        causes.add(currentThrowable);

        for (int i = 0; i < size - 1; i++) {
            Throwable newThrowable = new Throwable();
            currentThrowable.initCause(newThrowable);
            currentThrowable = newThrowable;
            causes.add(currentThrowable);
        }

        return causes;
    }

    Throwable generateThrowableStack(int size) {
        return generateThrowableList(size).get(0);
    }

    @Test
    void getCauses_returns_empty_list_if_Throwable_has_no_cause() {
        Throwable lonelyThrowable = new RuntimeException();
        assertTrue(Throwables.getCauses(lonelyThrowable).isEmpty());
    }

    @Test
    void getCauses_returns_list_of_causes() {
        assertEquals(19, Throwables.getCauses(generateThrowableStack(20)).size());
    }

    @Test
    void hasCause_returns_false_if_no_cause_with_supplied_type_is_found() {
        Throwable throwable = generateThrowableStack(10);
        assertFalse(hasCause(throwable, RuntimeException.class));
        assertFalse(hasCause(throwable, new RuntimeException()));
    }

    @Test
    void hasCause_returns_true_if_cause_with_supplied_type_is_found() {
        Throwable throwable = generateThrowableStack(10);
        Throwable runtimeException = getRootCause(throwable).initCause(new RuntimeException());

        assertTrue(hasCause(throwable, RuntimeException.class));
        assertTrue(hasCause(throwable, runtimeException));
    }

    @Test
    void hasCause_returns_false_if_cause_with_supplied_type_is_found_but_is_not_the_same_object_as_supplied_Throwable() {
        Throwable throwable = generateThrowableStack(10);
        Throwable runtimeException = getRootCause(throwable).initCause(new RuntimeException());

        assertFalse(hasCause(throwable, new RuntimeException()));
    }

    @Test
    void getRootCause_returns_the_rootCause_of_the_supplied_Throwable() {
        List<Throwable> throwables = generateThrowableList(10);
        Throwable firstThrowable = throwables.get(0);
        Throwable lastThrowable = throwables.get(9);

        assertEquals(lastThrowable, getRootCause(firstThrowable));
    }

    @Test
    void getRootCause_returns_the_supplied_Throwable_if_it_has_no_root_cause() {
        Throwable throwable = new Throwable();
        assertEquals(throwable, getRootCause(throwable));
    }

    @Test
    void getCauseAtLevel_returns_cause_at_supplied_depth_wrapped_in_Optional() {
        Throwable throwable = generateThrowableStack(5);
        Throwable cause = getRootCause(throwable);
        Optional<Throwable> causeAtLevel = getCauseAtLevel(throwable, 3);

        assertTrue(causeAtLevel.isPresent());
        assertEquals(cause, causeAtLevel.get());
    }

    @Test
    void getCauseAtLevel_returns_empty_Optional_if_depth_is_deeper_than_actual_depth() {
        Throwable throwable = generateThrowableStack(5);
        assertFalse(getCauseAtLevel(throwable, 4).isPresent());
    }
}
