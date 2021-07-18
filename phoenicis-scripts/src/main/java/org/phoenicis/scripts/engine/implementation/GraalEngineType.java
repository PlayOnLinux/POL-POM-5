package org.phoenicis.scripts.engine.implementation;

import org.graalvm.polyglot.Value;
import org.phoenicis.scripts.engine.ScriptEngineType;

import java.util.Map;

public class GraalEngineType implements ScriptEngineType<Value> {
    @Override
    public PhoenicisScriptEngine<Value> createScriptEngine() {
        return new PolyglotScriptEngine("js",
                Map.of("js.nashorn-compat", "true",
                        "js.experimental-foreign-object-prototype", "true"));
    }

    @Override
    public String toString() {
        return "graal.js";
    }
}
