package org.phoenicis.scripts.engine.implementation;

/**
 * A factory to create new {@link TypedScriptEngine} instances
 */
public interface TypedScriptEngineFactory {
    /**
     * Creates a new {@link TypedScriptEngine} instance for the given `type` input
     *
     * @param type The result type {@link Class} for the created script engine
     * @param <T>  The result type
     * @return The created script engine
     */
    <T> TypedScriptEngine<T> createScriptEngine(final Class<T> type);
}
