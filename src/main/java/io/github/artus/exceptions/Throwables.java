package io.github.artus.exceptions;

import java.util.*;
import java.util.stream.Collectors;

/**
 * The {@link Throwables} class can be used to unwrap any {@link Object} that is a subclass of the {@link Throwable} class.
 *
 * @author Artus Vranken
 */
public class Throwables {
    /**
     * Get the list of causes that lead up to the supplied Throwable, excluding the supplied Throwable itself.
     *
     * @param throwable The {@link Throwable}to get the causes from.
     * @return A List of {@link Throwable}s that are causes of the supplied {@link Throwable}.
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
     * Check if a supplied {@link Throwable} has another supplied {@link Throwable} as cause.
     * This method will only return true if the same {@link Throwable} instance as the supplied one is found.
     *
     * @param throwable The {@link Throwable} that needs its causes to be inspected.
     * @param cause     The {@link Throwable} that we want to check for presence.
     * @return <code>true</code> if the supplied {@link Throwable} has the cause, <code>false</code> if not.
     */
    public static boolean hasCause(Throwable throwable, Throwable cause) {
        return getCauses(throwable)
                .stream()
                .parallel()
                .anyMatch(aCause -> aCause.equals(cause));
    }

    /**
     * Check if a supplied {@link Throwable} has a {@link Throwable} with specific type as cause.
     *
     * @param throwable The {@link Throwable} that needs its causes to be inspected.
     * @param cause     The {@link Class} that we want to check for presence.
     * @return <code>true</code> if a cause was found with the supplied {@link Class} as type, <code>false</code> if not.
     */
    public static boolean hasCause(Throwable throwable, Class<?> cause) {
        return getCauses(throwable)
                .stream()
                .parallel()
                .anyMatch(cause::isInstance);
    }

    /**
     * Get the root cause of the supplied {@link Throwable}.
     *
     * @param throwable The {@link Throwable} from which the root cause will be fetched.
     * @return The {@link Throwable} that is the root cause of the supplied {@link Throwable}.
     */
    public static Throwable getRootCause(Throwable throwable) {
        return throwable.getCause() == null ? throwable : getRootCause(throwable.getCause());
    }

    /**
     * Get an {@link Optional} of the cause of a supplied {@link Throwable} at the requested level.
     * Returns an empty {@link Optional} if the the level exceeds the real amount of levels in the cause stack.
     *
     * @param throwable The {@link Throwable} where we want to get a cause at a specific level from.
     * @param depth     The depth of the cause. 0 will return the first cause of the supplied {@link Throwable}.}
     * @return An {@link Optional} of the cause if one was present at the supplied depth, an empty {@link Optional} if not.
     */
    public static Optional<Throwable> getCauseAtLevel(Throwable throwable, int depth) {
        List<Throwable> causes = getCauses(throwable);
        return causes.size() <= depth ? Optional.empty() : Optional.of(causes.get(depth));
    }

    /**
     * Get the first cause that is an instance of the supplied {@link Class}type. Has same behaviour as getFirstCauseOfType.
     *
     * @param throwable The {@link Throwable} where we want to get the first cause from.
     * @param type      The {@link Class} for which we want to get the first occurring cause from.
     * @param <T>       The type of the cause we want.
     * @return An {@link Optional} containing the first occurring cause of supplied {@link Class} type,
     * an empty {@link Optional} if none was found.
     */
    public static <T extends Throwable> Optional<T> getCause(Throwable throwable, Class<T> type) {
        return getFirstCauseOfType(throwable, type);
    }

    /**
     * Get the first occurring cause that is an instance of the supplied {@link Class} type.
     *
     * @param throwable The {@link Throwable} where we want to get the a cause from.
     * @param type      The {@link Class} for which we want to get the first occurrence for.
     * @param <T>       The type of the cause we want.
     * @return An {@link Optional} containing the first occurring cause that is an instance of supplied {@link Class} type,
     * an empty {@link Optional} if none was found.
     */
    public static <T extends Throwable> Optional<T> getFirstCauseOfType(Throwable throwable, Class<T> type) {
        Throwable cause = throwable.getCause();
        if (cause == null) return Optional.empty();
        return type.isInstance(cause)
                ? Optional.of(type.cast(cause))
                : getFirstCauseOfType(cause, type);
    }

    /**
     * Get the last occurring cause that is of the supplied {@link Class} type.
     *
     * @param throwable The {@link Throwable} where we want to get the a cause from.
     * @param type      The {@link Class} for which we want to get the last occurrence for.
     * @param <T>       The type of the cause we want.
     * @return An {@link Optional} containing the last occurring cause that is an instance of supplied {@link Class} type,
     * an empty {@link Optional} if none was found.
     */
    public static <T> Optional<T> getLastCauseOfType(Throwable throwable, Class<T> type) {
        return getLastCauseOfType(throwable, type, null);
    }

    /**
     * Get the last occurring cause that is of the supplied {@link Class} type.
     *
     * @param throwable The {@link Throwable} where we want to get the a cause from.
     * @param type      The {@link Class} for which we want to get the last occurrence for.
     * @param lastCause The {@link Throwable} that should be returned if no other cause was found.
     * @param <T>       The type of the cause we want.
     * @return An {@link Optional} containing the last occurring cause that is an instance of supplied {@link Class} type,
     * an empty {@link Optional} if none was found and <code>lastCause</code> is <code>null</code>, or an {@link Optional} containing the supplied <code>lastCause</code>.
     */
    private static <T> Optional<T> getLastCauseOfType(Throwable throwable, Class<T> type, Throwable lastCause) {
        Throwable cause = throwable.getCause();
        if (cause == null) return lastCause == null ? Optional.empty() : Optional.of(type.cast(lastCause));
        if (type.isInstance(cause)) lastCause = cause;
        return getLastCauseOfType(cause, type, lastCause);
    }

    /**
     * Get all causes that are an instance of the supplied {@link Class} type.
     *
     * @param throwable The {@link Throwable} that we want to get causes from.
     * @param type      The {@link Class} type that we want to get occurrences for.
     * @param <T>       The type of the cause we want.
     * @return A {@link List} containing all causes that are instance of the supplied {@link Class} type.
     */
    public static <T extends Throwable> List<T> getCaueseOfType(Throwable throwable, Class<T> type) {
        return getCauses(throwable)
                .stream()
                .filter(type::isInstance)
                .map(type::cast)
                .collect(Collectors.toList());
    }
}
