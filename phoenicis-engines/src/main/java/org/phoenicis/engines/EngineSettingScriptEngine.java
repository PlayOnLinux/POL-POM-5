package org.phoenicis.engines;

import org.graalvm.polyglot.Value;
import org.phoenicis.scripts.engine.implementation.PhoenicisScriptEngine;
import org.phoenicis.scripts.engine.implementation.TypedScriptEngine;

public class EngineSettingScriptEngine implements TypedScriptEngine<EngineSetting> {
    private final PhoenicisScriptEngine<Value> scriptEngine;

    public EngineSettingScriptEngine(PhoenicisScriptEngine<Value> scriptEngine) {
        super();

        this.scriptEngine = scriptEngine;
    }

    @Override
    public EngineSetting evaluate(String scriptId) {
        final String script = String.format("include(\"%s\")", scriptId);

        return scriptEngine.evaluate(script).newInstance().as(EngineSetting.class);
    }
}
