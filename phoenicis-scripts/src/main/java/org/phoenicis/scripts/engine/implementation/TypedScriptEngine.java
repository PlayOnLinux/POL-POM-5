package org.phoenicis.scripts.engine.implementation;

/**
 * A typed script engine used by Phoenicis
 *
 * @param <T> The result type of the script
 */
public interface TypedScriptEngine<T> {
    /**
     * Evaluates the script with the given id and returns its result cast to the given type
     *
     * @param scriptId The id of the evaluated script
     * @return The cast result of the evaluated script
     */
    T evaluate(String scriptId);
}
