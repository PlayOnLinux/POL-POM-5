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

package org.phoenicis.engines;

import org.graalvm.polyglot.Value;
import org.phoenicis.repository.dto.ApplicationDTO;
import org.phoenicis.repository.dto.CategoryDTO;
import org.phoenicis.repository.dto.RepositoryDTO;
import org.phoenicis.repository.dto.TypeDTO;
import org.phoenicis.scripts.engine.PhoenicisScriptEngineFactory;
import org.phoenicis.scripts.engine.implementation.PhoenicisScriptEngine;
import org.phoenicis.scripts.exceptions.ScriptException;
import org.phoenicis.scripts.interpreter.ScriptInterpreter;
import org.phoenicis.scripts.session.InteractiveScriptSession;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * manages the Verbs
 */
public class VerbsManager {
    private final ScriptInterpreter scriptInterpreter;

    private final PhoenicisScriptEngineFactory scriptEngineFactory;

    /**
     * Constructor
     *
     * @param scriptInterpreter The underlying script interpreter
     * @param scriptEngineFactory
     */
    public VerbsManager(ScriptInterpreter scriptInterpreter, PhoenicisScriptEngineFactory scriptEngineFactory) {
        this.scriptInterpreter = scriptInterpreter;
        this.scriptEngineFactory = scriptEngineFactory;
    }

    /**
     * Installs a Verb in a given container
     *
     * @param engineId ID of the engine which provides the Verb (e.g. "Wine")
     * @param container name of the container
     * @param verbId ID of the Verb
     * @param doneCallback callback executed after the script ran
     * @param errorCallback callback executed in case of an error
     */
    public void installVerb(String engineId, String container, String verbId, Runnable doneCallback,
            Consumer<Exception> errorCallback) {
        final InteractiveScriptSession interactiveScriptSession = scriptInterpreter.createInteractiveSession();

        final String script = String.format("include(\"%s\");", verbId);

        interactiveScriptSession.eval(script,
                output -> {
                    final Value verbClass = (Value) output;

                    try {
                        verbClass.invokeMember("install", container);
                    } catch (ScriptException se) {
                        errorCallback.accept(se);
                    }

                    doneCallback.run();
                },
                errorCallback);
    }

    /**
     * Installs a list of Verbs in a given container
     *
     * @param engineId ID of the engine which provides the Verbs (e.g. "Wine")
     * @param container name of the container
     * @param verbIds A list of verb ids
     * @param doneCallback callback executed after the scripts ran
     * @param errorCallback callback executed in case of an error
     */
    public void installVerbs(String engineId, String container, List<String> verbIds, Runnable doneCallback,
            Consumer<Exception> errorCallback) {
        if (verbIds.isEmpty()) {
            doneCallback.run();
        } else {
            final String verbId = verbIds.get(0);

            final List<String> remainingVerbIds = verbIds.subList(1, verbIds.size());

            installVerb(engineId, container, verbId,
                    // recursively install the remaining verbs in the list
                    () -> installVerbs(engineId, container, remainingVerbIds, doneCallback, errorCallback),
                    errorCallback);
        }
    }

    /**
     * Fetches the available Verbs
     *
     * @param repositoryDTO
     * @param callback
     */
    public void fetchAvailableVerbs(RepositoryDTO repositoryDTO, Consumer<Map<String, ApplicationDTO>> callback) {
        Map<String, ApplicationDTO> verbs = new HashMap<>();
        // get engine CategoryDTOs
        List<CategoryDTO> categoryDTOS = new ArrayList<>();
        for (TypeDTO typeDTO : repositoryDTO.getTypes()) {
            if (typeDTO.getId().equals("engines")) {
                categoryDTOS = typeDTO.getCategories();
            }
        }
        for (CategoryDTO engine : categoryDTOS) {
            for (ApplicationDTO applicationDTO : engine.getApplications()) {
                if (applicationDTO.getId().equals(engine.getId() + ".verbs")) {
                    verbs.put(engine.getId().replaceAll("^.*\\.", ""), applicationDTO);
                }
            }
        }
        callback.accept(verbs);
    }

    public boolean isVerbInstalled(String engineId, String verbId, String container) {
        final PhoenicisScriptEngine scriptEngine = scriptEngineFactory.createEngine();

        final String include = String.format("include(\"engines.%s.engine.implementation\");", engineId);

        final Value engineClass = (Value) scriptEngine.evalAndReturn(include, exception -> {
        });

        final Engine engine = engineClass.newInstance().as(Engine.class);

        return engine.isVerbRegistered(verbId, container);
    }
}
