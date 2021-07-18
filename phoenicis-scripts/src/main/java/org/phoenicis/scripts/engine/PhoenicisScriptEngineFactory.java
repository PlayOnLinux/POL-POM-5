/*
 * Copyright (C) 2015-2017 PÂRIS Quentin
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.phoenicis.scripts.engine;

import org.phoenicis.scripts.engine.implementation.PhoenicisScriptEngine;
import org.phoenicis.scripts.engine.injectors.EngineInjector;

import java.util.List;

public class PhoenicisScriptEngineFactory<T> {
    private final ScriptEngineType<T> type;
    private final List<EngineInjector<T>> engineInjectors;

    public PhoenicisScriptEngineFactory(ScriptEngineType<T> type, List<EngineInjector<T>> engineInjectors) {
        super();

        this.type = type;
        this.engineInjectors = engineInjectors;
    }

    public PhoenicisScriptEngine<T> createEngine() {
        final PhoenicisScriptEngine<T> phoenicisScriptEngine = this.type.createScriptEngine();

        engineInjectors.forEach(engineInjector -> engineInjector.injectInto(phoenicisScriptEngine));

        return phoenicisScriptEngine;
    }
}
