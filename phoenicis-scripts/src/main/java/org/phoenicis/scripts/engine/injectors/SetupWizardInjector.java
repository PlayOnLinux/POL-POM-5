package org.phoenicis.scripts.engine.injectors;

import org.graalvm.polyglot.Value;
import org.phoenicis.scripts.TriFunction;
import org.phoenicis.scripts.engine.implementation.PhoenicisScriptEngine;
import org.phoenicis.scripts.ui.InstallationType;
import org.phoenicis.scripts.wizard.UiSetupWizardFactory;
import org.phoenicis.scripts.wizard.UiSetupWizardImplementation;

import java.net.URI;
import java.util.Optional;

/**
 * Injects SetupWizard() function into a script engine
 */
public class SetupWizardInjector implements EngineInjector<Value> {
    private final UiSetupWizardFactory uiSetupWizardFactory;

    public SetupWizardInjector(UiSetupWizardFactory uiSetupWizardFactory) {
        this.uiSetupWizardFactory = uiSetupWizardFactory;
    }

    @Override
    public void injectInto(PhoenicisScriptEngine<Value> phoenicisScriptEngine) {
        final String installationTypeScript = String.format("var InstallationType = Java.type(\"%s\")", InstallationType.class.getCanonicalName());

        // define installation type class
        phoenicisScriptEngine.evaluate(installationTypeScript);

        phoenicisScriptEngine.put("SetupWizard", (TriFunction<InstallationType, String, Optional<URI>, UiSetupWizardImplementation>)
                (installationType, name, miniature) -> uiSetupWizardFactory.create(name, miniature, installationType));
    }
}
