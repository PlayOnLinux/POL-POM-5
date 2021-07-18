package org.phoenicis.scripts.engine.injectors;

import org.phoenicis.scripts.engine.implementation.PhoenicisScriptEngine;

/**
 * Injects some code into a Script Engine
 */
public interface EngineInjector<T> {
    /**
     * Injects component into an engine engine
     *
     * @param phoenicisScriptEngine The engine to be injected in
     */
    void injectInto(PhoenicisScriptEngine<T> phoenicisScriptEngine);
}
