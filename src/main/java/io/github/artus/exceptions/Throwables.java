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

    /**
     * Get the first cause that is of the supplied type. Has same behaviour as getFirstCauseOfType.
     *
     * @param throwable
     * @param type
     * @return
     */
    public static <T extends Throwable> Optional<T> getCause(Throwable throwable, Class<T> type) {
        return getFirstCauseOfType(throwable, type);
    }

    /**
     * Get the first cause that is of the supplied type. Has same behaviour as getFirstCauseOfType.
     *
     * @param throwable
     * @param type
     * @return
     */
    public static <T extends Throwable> Optional<T> getFirstCauseOfType(Throwable throwable, Class<T> type) {
        Throwable cause = throwable.getCause();
        if (cause == null) return Optional.empty();
        return type.isInstance(cause)
                ? Optional.of(type.cast(cause))
                : getFirstCauseOfType(cause, type);
    }

    /**
     * Get the last cause that is of the supplied type.
     *
     * @param throwable
     * @param type
     * @return
     */
    public static <T> Optional<T> getLastCauseOfType(Throwable throwable, Class<T> type) {
        return getLastCauseOfType(throwable, type, null);
    }

    /**
     * Get the last cause that is of the supplied type.
     * @param throwable
     * @param type
     * @param lastCause
     * @return
     */
    private static <T> Optional<T> getLastCauseOfType(Throwable throwable, Class<T> type, Throwable lastCause) {
        Throwable cause = throwable.getCause();
        if (cause == null) return lastCause == null ? Optional.empty() : Optional.of(type.cast(lastCause));
        if (type.isInstance(cause)) lastCause = cause;
        return getLastCauseOfType(cause, type, lastCause);
    }
}
