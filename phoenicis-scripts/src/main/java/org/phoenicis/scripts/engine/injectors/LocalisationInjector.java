package org.phoenicis.scripts.engine.injectors;

import org.graalvm.polyglot.Value;
import org.phoenicis.scripts.engine.implementation.PhoenicisScriptEngine;

/**
 * Injects tr() function into a Script Engine
 */
public class LocalisationInjector implements EngineInjector<Value> {
    @Override
    public void injectInto(PhoenicisScriptEngine<Value> phoenicisScriptEngine) {
        phoenicisScriptEngine.evaluate("var tr = Packages.org.phoenicis.configuration.localisation.Localisation.tr;");
    }
}
