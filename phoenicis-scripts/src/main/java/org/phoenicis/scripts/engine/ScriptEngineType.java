package org.phoenicis.scripts.engine;

import org.graalvm.polyglot.Value;
import org.phoenicis.scripts.engine.implementation.GraalEngineType;
import org.phoenicis.scripts.engine.implementation.PhoenicisScriptEngine;

/**
 * A script engine type supported by Phoenicis
 *
 * @param <T> The internal script result type used by the script engine
 */
public interface ScriptEngineType<T> {
    // convenience instance
    ScriptEngineType<Value> GRAAL = new GraalEngineType();

    /**
     * Creates a new script engine
     *
     * @return The created script engine
     */
    PhoenicisScriptEngine<T> createScriptEngine();
}