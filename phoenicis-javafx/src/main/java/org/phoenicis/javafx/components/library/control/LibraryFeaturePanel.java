package org.phoenicis.javafx.components.library.control;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Tab;
import org.apache.commons.lang.StringUtils;
import org.graalvm.polyglot.Value;
import org.phoenicis.javafx.components.common.panelstates.None;
import org.phoenicis.javafx.components.common.panelstates.OpenDetailsPanel;
import org.phoenicis.javafx.components.common.control.FeaturePanel;
import org.phoenicis.javafx.components.library.skin.LibraryFeaturePanelSkin;
import org.phoenicis.javafx.controller.library.console.ConsoleController;
import org.phoenicis.javafx.dialogs.ErrorDialog;
import org.phoenicis.javafx.dialogs.SimpleConfirmDialog;
import org.phoenicis.javafx.settings.JavaFxSettingsManager;
import org.phoenicis.javafx.views.mainwindow.console.ConsoleTab;
import org.phoenicis.javafx.views.mainwindow.library.LibraryFilter;
import org.phoenicis.library.ShortcutManager;
import org.phoenicis.library.ShortcutRunner;
import org.phoenicis.library.dto.ShortcutCategoryDTO;
import org.phoenicis.library.dto.ShortcutCreationDTO;
import org.phoenicis.library.dto.ShortcutDTO;
import org.phoenicis.scripts.session.InteractiveScriptSession;
import org.phoenicis.scripts.interpreter.ScriptInterpreter;

import java.util.Collections;

import static org.phoenicis.configuration.localisation.Localisation.tr;

/**
 * The component shown inside the Phoenicis "Library" tab
 */
public class LibraryFeaturePanel extends FeaturePanel<LibraryFeaturePanel, LibraryFeaturePanelSkin> {
    /**
     * The name of the application
     */
    private final StringProperty applicationName;

    /**
     * The path leading to the containers
     */
    private final StringProperty containersPath;

    /**
     * The library filter
     */
    private final ObjectProperty<LibraryFilter> filter;

    /**
     * The JavaFX settings manager
     */
    private final ObjectProperty<JavaFxSettingsManager> javaFxSettingsManager;

    /**
     * The script interpreter
     */
    private final ObjectProperty<ScriptInterpreter> scriptInterpreter;

    /**
     * The object mapper
     */
    private final ObjectProperty<ObjectMapper> objectMapper;

    /**
     * A list of all known shortcut categories
     */
    private final ObservableList<ShortcutCategoryDTO> categories;

    /**
     * A list of all opened tabs
     */
    private final ObservableList<Tab> tabs;

    /**
     * The currently open tab
     */
    private final ObjectProperty<Tab> selectedTab;

    /**
     * The console controller
     */
    private final ObjectProperty<ConsoleController> consoleController;

    /**
     * The shortcut runner
     */
    private final ObjectProperty<ShortcutRunner> shortcutRunner;

    /**
     * The shortcut manager
     */
    private final ObjectProperty<ShortcutManager> shortcutManager;

    /**
     * The currently selected shortcut
     */
    private final ObjectProperty<ShortcutDTO> selectedShortcut;

    /**
     * The currently opened details panel
     */
    private final ObjectProperty<OpenDetailsPanel> openedDetailsPanel;

    /**
     * Constructor
     */
    public LibraryFeaturePanel() {
        super();

        this.applicationName = new SimpleStringProperty();
        this.containersPath = new SimpleStringProperty();
        this.filter = new SimpleObjectProperty<>();
        this.javaFxSettingsManager = new SimpleObjectProperty<>();
        this.scriptInterpreter = new SimpleObjectProperty<>();
        this.objectMapper = new SimpleObjectProperty<>();
        this.categories = FXCollections.observableArrayList();
        this.tabs = FXCollections.observableArrayList();
        this.consoleController = new SimpleObjectProperty<>();
        this.shortcutRunner = new SimpleObjectProperty<>();
        this.shortcutManager = new SimpleObjectProperty<>();
        this.selectedTab = new SimpleObjectProperty<>();
        this.selectedShortcut = new SimpleObjectProperty<>();
        this.openedDetailsPanel = new SimpleObjectProperty<>(new None());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LibraryFeaturePanelSkin createSkin() {
        return new LibraryFeaturePanelSkin(this);
    }

    /**
     * Creates a new shortcut
     *
     * @param shortcutCreationDTO DTO describing the new shortcut
     */
    public void createShortcut(ShortcutCreationDTO shortcutCreationDTO) {
        // get container
        // TODO: smarter way using container manager
        final String executablePath = shortcutCreationDTO.getExecutable().getAbsolutePath();
        if (!executablePath.startsWith(getContainersPath())) {
            Platform.runLater(() -> {
                final ErrorDialog errorDialog = ErrorDialog.builder()
                        .withMessage(tr("Creating shortcut to executable outside of a container is not supported"))
                        .withOwner(getScene().getWindow())
                        .build();

                errorDialog.showAndWait();
            });
        } else {
            final String pathInContainers = executablePath.replace(getContainersPath(), "");
            final String[] split = pathInContainers.split("/");
            final String engineContainer = split[0];
            final String engine = StringUtils.capitalize(engineContainer).replace("prefix", "");
            // TODO: better way to get engine ID
            final String engineId = engine.toLowerCase();
            final String container = split[1];

            final InteractiveScriptSession interactiveScriptSession = getScriptInterpreter().createInteractiveSession();

            final String scriptInclude = "const Shortcut = include(\"engines." + engineId + ".shortcuts." + engineId
                    + "\");";

            interactiveScriptSession.eval(scriptInclude,
                    ignored -> interactiveScriptSession.eval("new Shortcut()",
                            output -> {
                                final Value shortcutObject = (Value) output;

                                shortcutObject.invokeMember("name", shortcutCreationDTO.getName());
                                shortcutObject.invokeMember("category", shortcutCreationDTO.getCategory());
                                shortcutObject.invokeMember("description", shortcutCreationDTO.getDescription());
                                shortcutObject.invokeMember("miniature", shortcutCreationDTO.getMiniature());
                                shortcutObject.invokeMember("search", shortcutCreationDTO.getExecutable().getName());
                                shortcutObject.invokeMember("prefix", container);
                                shortcutObject.invokeMember("create");
                            },
                            e -> Platform.runLater(() -> {
                                final ErrorDialog errorDialog = ErrorDialog.builder()
                                        .withMessage(tr("Error while creating shortcut"))
                                        .withException(e)
                                        .withOwner(getScene().getWindow())
                                        .build();

                                errorDialog.showAndWait();
                            })),
                    e -> Platform.runLater(() -> {
                        final ErrorDialog errorDialog = ErrorDialog.builder()
                                .withMessage(tr("Error while creating shortcut"))
                                .withException(e)
                                .withOwner(getScene().getWindow())
                                .build();

                        errorDialog.showAndWait();
                    }));
        }
    }

    /**
     * Executes/runs a given shortcut
     *
     * @param shortcut The shortcut to be executed
     */
    public void runShortcut(ShortcutDTO shortcut) {
        getShortcutRunner().run(shortcut, Collections.emptyList(), e -> Platform.runLater(() -> {
            final ErrorDialog errorDialog = ErrorDialog.builder()
                    .withMessage(tr("Error"))
                    .withException(e)
                    .withOwner(getScene().getWindow())
                    .build();

            errorDialog.showAndWait();
        }));
    }

    /**
     * Stops the application referenced by a given shortcut
     *
     * @param shortcut The shortcut of the application to be stopped
     */
    public void stopShortcut(ShortcutDTO shortcut) {
        getShortcutRunner().stop(shortcut, e -> Platform.runLater(() -> {
            final ErrorDialog errorDialog = ErrorDialog.builder()
                    .withMessage(tr("Error"))
                    .withException(e)
                    .withOwner(getScene().getWindow())
                    .build();

            errorDialog.showAndWait();
        }));
    }

    /**
     * Removes a given shortcut
     *
     * @param shortcut The shortcut to be removed
     */
    public void uninstallShortcut(ShortcutDTO shortcut) {
        final String shortcutName = shortcut.getInfo().getName();

        final SimpleConfirmDialog confirmMessage = SimpleConfirmDialog.builder()
                .withTitle(tr("Uninstall {0}", shortcutName))
                .withMessage(tr("Are you sure you want to uninstall {0}?", shortcutName))
                .withOwner(getScene().getWindow())
                .withResizable(true)
                .withYesCallback(() -> getShortcutManager().uninstallFromShortcut(shortcut, e -> {
                    final ErrorDialog errorDialog = ErrorDialog.builder()
                            .withMessage(tr("Error while uninstalling {0}", shortcutName))
                            .withException(e)
                            .withOwner(getScene().getWindow())
                            .build();

                    errorDialog.showAndWait();
                }))
                .build();

        confirmMessage.showAndCallback();
    }

    /**
     * Opens a new console tab
     */
    public void openConsole() {
        final ConsoleTab console = getConsoleController().createConsole();

        getTabs().add(console);
        setSelectedTab(console);
    }

    public String getApplicationName() {
        return this.applicationName.get();
    }

    public StringProperty applicationNameProperty() {
        return this.applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName.set(applicationName);
    }

    public String getContainersPath() {
        return this.containersPath.get();
    }

    public StringProperty containersPathProperty() {
        return this.containersPath;
    }

    public void setContainersPath(String containersPath) {
        this.containersPath.set(containersPath);
    }

    public LibraryFilter getFilter() {
        return this.filter.get();
    }

    public ObjectProperty<LibraryFilter> filterProperty() {
        return this.filter;
    }

    public void setFilter(LibraryFilter filter) {
        this.filter.set(filter);
    }

    public JavaFxSettingsManager getJavaFxSettingsManager() {
        return this.javaFxSettingsManager.get();
    }

    public ObjectProperty<JavaFxSettingsManager> javaFxSettingsManagerProperty() {
        return this.javaFxSettingsManager;
    }

    public void setJavaFxSettingsManager(JavaFxSettingsManager javaFxSettingsManager) {
        this.javaFxSettingsManager.set(javaFxSettingsManager);
    }

    public ScriptInterpreter getScriptInterpreter() {
        return this.scriptInterpreter.get();
    }

    public ObjectProperty<ScriptInterpreter> scriptInterpreterProperty() {
        return this.scriptInterpreter;
    }

    public void setScriptInterpreter(ScriptInterpreter scriptInterpreter) {
        this.scriptInterpreter.set(scriptInterpreter);
    }

    public ObjectMapper getObjectMapper() {
        return this.objectMapper.get();
    }

    public ObjectProperty<ObjectMapper> objectMapperProperty() {
        return this.objectMapper;
    }

    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper.set(objectMapper);
    }

    public ObservableList<ShortcutCategoryDTO> getCategories() {
        return this.categories;
    }

    public ObservableList<Tab> getTabs() {
        return this.tabs;
    }

    public Tab getSelectedTab() {
        return this.selectedTab.get();
    }

    public ObjectProperty<Tab> selectedTabProperty() {
        return this.selectedTab;
    }

    public void setSelectedTab(Tab selectedTab) {
        this.selectedTab.set(selectedTab);
    }

    public ConsoleController getConsoleController() {
        return this.consoleController.get();
    }

    public ObjectProperty<ConsoleController> consoleControllerProperty() {
        return this.consoleController;
    }

    public void setConsoleController(ConsoleController consoleController) {
        this.consoleController.set(consoleController);
    }

    public ShortcutRunner getShortcutRunner() {
        return this.shortcutRunner.get();
    }

    public ObjectProperty<ShortcutRunner> shortcutRunnerProperty() {
        return this.shortcutRunner;
    }

    public void setShortcutRunner(ShortcutRunner shortcutRunner) {
        this.shortcutRunner.set(shortcutRunner);
    }

    public ShortcutManager getShortcutManager() {
        return this.shortcutManager.get();
    }

    public ObjectProperty<ShortcutManager> shortcutManagerProperty() {
        return this.shortcutManager;
    }

    public void setShortcutManager(ShortcutManager shortcutManager) {
        this.shortcutManager.set(shortcutManager);
    }

    public ShortcutDTO getSelectedShortcut() {
        return this.selectedShortcut.get();
    }

    public ObjectProperty<ShortcutDTO> selectedShortcutProperty() {
        return this.selectedShortcut;
    }

    public void setSelectedShortcut(ShortcutDTO selectedShortcut) {
        this.selectedShortcut.set(selectedShortcut);
    }

    public OpenDetailsPanel getOpenedDetailsPanel() {
        return this.openedDetailsPanel.get();
    }

    public ObjectProperty<OpenDetailsPanel> openedDetailsPanelProperty() {
        return this.openedDetailsPanel;
    }

    public void setOpenedDetailsPanel(OpenDetailsPanel openDetailsPanel) {
        this.openedDetailsPanel.set(openDetailsPanel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void closeDetailsPanel() {
        // deselect the currently selected shortcut
        setSelectedShortcut(null);
        // close the details panel
        setOpenedDetailsPanel(new None());
    }
}
