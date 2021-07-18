package org.phoenicis.scripts.engine.injectors;

import org.apache.commons.io.IOUtils;
import org.graalvm.polyglot.Value;
import org.phoenicis.scripts.engine.implementation.PhoenicisScriptEngine;
import org.phoenicis.scripts.exceptions.ScriptException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Includes utils.js
 */
public class ScriptUtilitiesInjector implements EngineInjector<Value> {
    @Override
    public void injectInto(PhoenicisScriptEngine<Value> phoenicisScriptEngine) {
        try {
            String utilsScript = IOUtils.toString(getClass().getResourceAsStream("utils.js"), StandardCharsets.UTF_8);

            phoenicisScriptEngine.evaluate(utilsScript);
        } catch (IOException e) {
            throw new ScriptException("error loading utils script", e);
        }
    }
}
