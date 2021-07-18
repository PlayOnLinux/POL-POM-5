package org.phoenicis.scripts.engine.implementation;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;

import java.util.Map;

/**
 * A {@link PhoenicisScriptEngine} wrapping around a polyglot {@link Context} object defined by Graal
 */
public class PolyglotScriptEngine implements PhoenicisScriptEngine<Value> {
    /**
     * The scripting language
     */
    private final String language;

    /**
     * The context representing the handle to the scripting engine
     */
    private final Context context;

    /**
     * Constructor
     *
     * @param language The language name
     * @param options  A map of options for the Polyglot context
     */
    public PolyglotScriptEngine(String language, Map<String, String> options) {
        super();

        this.language = language;
        this.context = Context.newBuilder(language)
                .allowExperimentalOptions(true)
                .allowHostAccess(true)
                .options(options).build();
    }

    @Override
    public void put(String key, Object value) {
        this.context.getBindings(this.language).putMember(key, value);
    }

    @Override
    public Value evaluate(String script) {
        return this.context.eval(this.language, script);
    }
}
