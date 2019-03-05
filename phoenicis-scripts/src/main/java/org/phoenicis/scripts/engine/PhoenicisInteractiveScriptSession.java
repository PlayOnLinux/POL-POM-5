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

import org.phoenicis.scripts.interpreter.InteractiveScriptSession;

import java.util.function.Consumer;

public class PhoenicisInteractiveScriptSession implements InteractiveScriptSession {
    private final PhoenicisScriptContext phoenicisScriptContext;

    public PhoenicisInteractiveScriptSession(PhoenicisScriptContextFactory phoenicisScriptContextFactory) {
        this.phoenicisScriptContext = phoenicisScriptContextFactory.createScriptContext();
    }

    @Override
    public void eval(String evaluation, Consumer<Object> responseCallback, Consumer<Exception> errorCallback) {
        responseCallback.accept(phoenicisScriptContext.evalAndReturn(evaluation, errorCallback));
    }
}
