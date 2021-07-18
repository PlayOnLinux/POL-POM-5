package org.phoenicis.javafx.components.engine.control;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import org.phoenicis.engines.Engine;
import org.phoenicis.engines.EnginesManager;
import org.phoenicis.engines.dto.EngineCategoryDTO;
import org.phoenicis.engines.dto.EngineDTO;
import org.phoenicis.javafx.components.common.control.FeaturePanel;
import org.phoenicis.javafx.components.engine.skin.EnginesFeaturePanelSkin;
import org.phoenicis.javafx.dialogs.ErrorDialog;
import org.phoenicis.javafx.dialogs.SimpleConfirmDialog;
import org.phoenicis.javafx.settings.JavaFxSettingsManager;
import org.phoenicis.javafx.views.mainwindow.engines.EnginesFilter;

import static org.phoenicis.configuration.localisation.Localisation.tr;

public class EnginesFeaturePanel extends FeaturePanel<EnginesFeaturePanel, EnginesFeaturePanelSkin> {
    private final ObjectProperty<EnginesFilter> filter;

    private final ObjectProperty<JavaFxSettingsManager> javaFxSettingsManager;

    private final StringProperty enginesPath;

    private final ObjectProperty<EnginesManager> enginesManager;

    private final ObservableMap<String, Engine> engines;

    private final ObjectProperty<Engine> engine;

    private final ObservableList<EngineCategoryDTO> engineCategories;

    private final ObjectProperty<EngineDTO> engineDTO;

    public EnginesFeaturePanel() {
        super();

        this.filter = new SimpleObjectProperty<>();
        this.javaFxSettingsManager = new SimpleObjectProperty<>();
        this.enginesPath = new SimpleStringProperty();
        this.enginesManager = new SimpleObjectProperty<>();
        this.engines = FXCollections.observableHashMap();
        this.engine = new SimpleObjectProperty<>();
        this.engineCategories = FXCollections.observableArrayList();
        this.engineDTO = new SimpleObjectProperty<>();
    }

    @Override
    public EnginesFeaturePanelSkin createSkin() {
        return new EnginesFeaturePanelSkin(this);
    }

    public void refreshEngineCategory(final EngineCategoryDTO engineCategory) {
        // TODO: better way to get engine ID
        final String engineId = engineCategory.getName().toLowerCase();

        getEnginesManager().fetchAvailableVersions(engineId,
                versions -> {
                    final EngineCategoryDTO newEngineCategory = new EngineCategoryDTO.Builder(engineCategory)
                            .withSubCategories(versions)
                            .build();

                    Platform.runLater(() -> {
                        getEngineCategories().remove(engineCategory);
                        getEngineCategories().add(newEngineCategory);
                    });
                },
                e -> Platform.runLater(() -> {
                    final ErrorDialog errorDialog = ErrorDialog.builder()
                            .withMessage(tr("Error"))
                            .withException(e)
                            .withOwner(getScene().getWindow())
                            .build();

                    errorDialog.showAndWait();
                }));
    }

    public void installEngine(final EngineDTO engineDTO) {
        final SimpleConfirmDialog confirmMessage = SimpleConfirmDialog.builder()
                .withTitle(tr("Install {0}", engineDTO.getVersion()))
                .withMessage(tr("Are you sure you want to install {0}?", engineDTO.getVersion()))
                .withOwner(getScene().getWindow())
                .withResizable(true)
                .withYesCallback(() -> getEnginesManager().getEngine(engineDTO.getId(),
                        engine -> {
                            engine.install(engineDTO.getSubCategory(), engineDTO.getVersion());

                            // TODO: better way to get engine ID
                            final String engineId = engineDTO.getId().toLowerCase();

                            final EngineCategoryDTO engineCategory = getEngineCategories().stream()
                                    .filter(engineCategoryDTO -> engineCategoryDTO.getName().equals(engineId))
                                    .findFirst().orElseThrow();

                            refreshEngineCategory(engineCategory);
                        }, e -> Platform.runLater(() -> {
                            final ErrorDialog errorDialog = ErrorDialog.builder()
                                    .withMessage(tr("Error"))
                                    .withException(e)
                                    .withOwner(getScene().getWindow())
                                    .build();

                            errorDialog.showAndWait();
                        })))
                .build();

        confirmMessage.showAndCallback();
    }

    public void deleteEngine(final EngineDTO engineDTO) {
        final SimpleConfirmDialog confirmMessage = SimpleConfirmDialog.builder()
                .withTitle(tr("Delete {0}", engineDTO.getVersion()))
                .withMessage(tr("Are you sure you want to delete {0}?", engineDTO.getVersion()))
                .withOwner(getScene().getWindow())
                .withResizable(true)
                .withYesCallback(() -> getEnginesManager().getEngine(engineDTO.getId(),
                        engine -> {
                            engine.delete(engineDTO.getSubCategory(), engineDTO.getVersion());

                            // TODO: better way to get engine ID
                            final String engineId = engineDTO.getId().toLowerCase();

                            final EngineCategoryDTO engineCategory = getEngineCategories().stream()
                                    .filter(engineCategoryDTO -> engineCategoryDTO.getName().equals(engineId))
                                    .findFirst().orElseThrow();

                            refreshEngineCategory(engineCategory);
                        }, e -> Platform.runLater(() -> {
                            final ErrorDialog errorDialog = ErrorDialog.builder()
                                    .withMessage(tr("Error"))
                                    .withException(e)
                                    .withOwner(getScene().getWindow())
                                    .build();

                            errorDialog.showAndWait();
                        })))
                .build();

        confirmMessage.showAndCallback();
    }

    public EnginesFilter getFilter() {
        return filter.get();
    }

    public ObjectProperty<EnginesFilter> filterProperty() {
        return filter;
    }

    public void setFilter(EnginesFilter filter) {
        this.filter.set(filter);
    }

    public JavaFxSettingsManager getJavaFxSettingsManager() {
        return javaFxSettingsManager.get();
    }

    public ObjectProperty<JavaFxSettingsManager> javaFxSettingsManagerProperty() {
        return javaFxSettingsManager;
    }

    public void setJavaFxSettingsManager(JavaFxSettingsManager javaFxSettingsManager) {
        this.javaFxSettingsManager.set(javaFxSettingsManager);
    }

    public String getEnginesPath() {
        return enginesPath.get();
    }

    public StringProperty enginesPathProperty() {
        return enginesPath;
    }

    public void setEnginesPath(String enginesPath) {
        this.enginesPath.set(enginesPath);
    }

    public ObservableMap<String, Engine> getEngines() {
        return engines;
    }

    public Engine getEngine() {
        return engine.get();
    }

    public ObjectProperty<Engine> engineProperty() {
        return engine;
    }

    public void setEngine(Engine engine) {
        this.engine.set(engine);
    }

    public ObservableList<EngineCategoryDTO> getEngineCategories() {
        return engineCategories;
    }

    public EngineDTO getEngineDTO() {
        return engineDTO.get();
    }

    public ObjectProperty<EngineDTO> engineDTOProperty() {
        return engineDTO;
    }

    public void setEngineDTO(EngineDTO engineDTO) {
        this.engineDTO.set(engineDTO);
    }

    public EnginesManager getEnginesManager() {
        return enginesManager.get();
    }

    public ObjectProperty<EnginesManager> enginesManagerProperty() {
        return enginesManager;
    }

    public void setEnginesManager(EnginesManager enginesManager) {
        this.enginesManager.set(enginesManager);
    }
}
