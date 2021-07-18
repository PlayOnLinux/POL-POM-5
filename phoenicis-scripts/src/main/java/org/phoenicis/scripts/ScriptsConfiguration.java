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

package org.phoenicis.scripts;

import org.phoenicis.multithreading.MultithreadingConfiguration;
import org.phoenicis.repository.RepositoryConfiguration;
import org.phoenicis.scripts.engine.PhoenicisScriptEngineFactory;
import org.phoenicis.scripts.engine.ScriptEngineType;
import org.phoenicis.scripts.engine.injectors.*;
import org.phoenicis.scripts.interpreter.BackgroundScriptInterpreter;
import org.phoenicis.scripts.interpreter.PhoenicisScriptInterpreter;
import org.phoenicis.scripts.interpreter.ScriptFetcher;
import org.phoenicis.scripts.interpreter.ScriptInterpreter;
import org.phoenicis.scripts.wizard.WizardConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.List;

@Configuration
@Import(WizardConfiguration.class)
public class ScriptsConfiguration {
    @Autowired
    private WizardConfiguration wizardConfiguration;

    @Autowired
    private RepositoryConfiguration repositoryConfiguration;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private MultithreadingConfiguration multithreadingConfiguration;

    @Bean
    public PhoenicisScriptEngineFactory<org.graalvm.polyglot.Value> graalScriptEngineFactory() {
        final List<EngineInjector<org.graalvm.polyglot.Value>> injectors = List.of(new ScriptUtilitiesInjector(),
                new BeanInjector(applicationContext), new SetupWizardInjector(wizardConfiguration.setupWizardFactory()),
                new IncludeInjector(scriptFetcher()), new LocalisationInjector());

        return new PhoenicisScriptEngineFactory<>(ScriptEngineType.GRAAL, injectors);
    }

    @Bean
    public ScriptFetcher scriptFetcher() {
        return new ScriptFetcher(repositoryConfiguration.repositoryManager());
    }

    @Bean
    public ScriptInterpreter scriptInterpreter() {
        return new BackgroundScriptInterpreter(graalScriptInterpreter(),
                multithreadingConfiguration.scriptExecutorService());
    }

    @Bean
    ScriptInterpreter graalScriptInterpreter() {
        return new PhoenicisScriptInterpreter(graalScriptEngineFactory());
    }
}
