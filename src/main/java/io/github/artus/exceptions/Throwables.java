package io.github.artus.exceptions;

import java.util.*;
import java.util.function.Predicate;

/**
 * The Throwables class can be used to unwrap any Object that is a subclass of the Throwable class.
 *
 */
public class Throwables
{
    /**
     * Get the list of causes that lead up to the supplied Throwable, excluding the supplied Throwable itself.
     *
     * @param throwable
     * @return
     */
    public static List<Throwable> getCauses(Throwable throwable) {
        Throwable cause = throwable.getCause();
        if (cause == null) return Collections.emptyList();
        List<Throwable> causes = new ArrayList<Throwable>();
        causes.add(cause);
        causes.addAll(getCauses(cause));
        return causes;
    }

    /**
     * Check if a supplied Throwable has another supplied Throwable as cause.
     *
     * @param throwable
     * @param cause
     * @return
     */
    public static boolean hasCause(Throwable throwable, Throwable cause) {
        return getCauses(throwable)
                .stream()
                .parallel()
                .anyMatch(aCause -> aCause.equals(cause));
    }

    /**
     * Check if a supplied Throwable has a Throwable with specific type as cause.
     *
     * @param throwable
     * @param cause
     * @return
     */
    public static boolean hasCause(Throwable throwable, Class<?> cause) {
        return getCauses(throwable)
                .stream()
                .parallel()
                .anyMatch(cause::isInstance);
    }

    /**
     * Get the root cause of the supplied Throwable.
     *
     * @param throwable
     * @return
     */
    public static Throwable getRootCause(Throwable throwable) {
        return throwable.getCause() == null ? throwable : getRootCause(throwable.getCause());
    }

    /**
     * Get an Optional of the cause of a supplied Throwable at the requested level.
     * Returns an empty Optional if the the level exceeds the real amount of levels in the cause stack.
     *
     * @param throwable
     * @param depth
     * @return
     */
    public static Optional<Throwable> getCauseAtLevel(Throwable throwable, int depth) {
        List<Throwable> causes = getCauses(throwable);
        return causes.size() <= depth ? Optional.empty() : Optional.of(causes.get(depth));
    }
}
