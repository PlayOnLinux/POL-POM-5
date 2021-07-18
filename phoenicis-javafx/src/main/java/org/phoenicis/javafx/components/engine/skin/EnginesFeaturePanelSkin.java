package org.phoenicis.javafx.components.engine.skin;

import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectExpression;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.phoenicis.engines.dto.EngineCategoryDTO;
import org.phoenicis.javafx.collections.ConcatenatedList;
import org.phoenicis.javafx.collections.MappedList;
import org.phoenicis.javafx.components.common.control.DetailsPanel;
import org.phoenicis.javafx.components.common.control.SidebarBase;
import org.phoenicis.javafx.components.common.skin.FeaturePanelSkin;
import org.phoenicis.javafx.components.common.widgets.utils.ListWidgetType;
import org.phoenicis.javafx.components.engine.control.EngineInformationPanel;
import org.phoenicis.javafx.components.engine.control.EngineSidebar;
import org.phoenicis.javafx.components.engine.control.EngineSubCategoryPanel;
import org.phoenicis.javafx.components.engine.control.EnginesFeaturePanel;
import org.phoenicis.javafx.settings.JavaFxSettingsManager;
import org.phoenicis.javafx.utils.StringBindings;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class EnginesFeaturePanelSkin extends FeaturePanelSkin<EnginesFeaturePanel, EnginesFeaturePanelSkin> {
    private final ObjectProperty<ListWidgetType> selectedListWidget;

    private final ObservableList<Tab> engineSubCategoryTabs;

    /**
     * Constructor
     *
     * @param control The control belonging to the skin
     */
    public EnginesFeaturePanelSkin(EnginesFeaturePanel control) {
        super(control);

        this.selectedListWidget = new SimpleObjectProperty<>();
        this.engineSubCategoryTabs = createEngineSubCategoryTabs();
    }

    private ObservableList<Tab> createEngineSubCategoryTabs() {
        // initialize the engines sub category panels
        final MappedList<List<EngineSubCategoryPanel>, EngineCategoryDTO> engineSubCategoryPanelGroups =
                new MappedList<>(getControl().getEngineCategories(), engineCategory ->
                        engineCategory.getSubCategories().stream().map(engineSubCategory -> {
                            final EngineSubCategoryPanel engineSubCategoryPanel = new EngineSubCategoryPanel();

                            engineSubCategoryPanel.setEngineCategory(engineCategory);
                            engineSubCategoryPanel.setEngineSubCategory(engineSubCategory);

                            engineSubCategoryPanel.enginesPathProperty().bind(getControl().enginesPathProperty());
                            engineSubCategoryPanel.filterProperty().bind(getControl().filterProperty());
                            engineSubCategoryPanel.engineProperty().bind(Bindings.createObjectBinding(() -> {
                                final String engineCategoryName = engineCategory.getName().toLowerCase();

                                return getControl().getEngines().get(engineCategoryName);
                            }, getControl().getEngines()));

                            engineSubCategoryPanel.selectedListWidgetProperty().bind(this.selectedListWidget);

                            engineSubCategoryPanel.setOnEngineSelect((engineDTO, engine) -> {
                                getControl().setEngineDTO(engineDTO);
                                getControl().setEngine(engine);
                            });

                            return engineSubCategoryPanel;
                        }).collect(Collectors.toList()));

        final ConcatenatedList<EngineSubCategoryPanel> engineSubCategoryPanels = ConcatenatedList
                .create(engineSubCategoryPanelGroups);

        final FilteredList<EngineSubCategoryPanel> filteredEngineSubCategoryPanels = engineSubCategoryPanels
                // sort the engine sub category panels alphabetically
                .sorted(Comparator
                        .comparing(engineSubCategoryPanel -> engineSubCategoryPanel.getEngineSubCategory().getName()))
                // filter the engine sub category panels, so that only the visible panels remain
                .filtered(getControl().getFilter()::filter);

        filteredEngineSubCategoryPanels.predicateProperty().bind(
                Bindings.createObjectBinding(() -> getControl().getFilter()::filter,
                        getControl().getFilter().searchTermProperty(),
                        getControl().getFilter().showInstalledProperty(),
                        getControl().getFilter().showNotInstalledProperty()));

        // map the panels to tabs
        return new MappedList<>(filteredEngineSubCategoryPanels,
                engineSubCategoryPanel -> new Tab(engineSubCategoryPanel.getEngineSubCategory().getDescription(),
                        engineSubCategoryPanel));
    }

    @Override
    public ObjectExpression<SidebarBase<?, ?, ?>> createSidebar() {
        final EngineSidebar sidebar = new EngineSidebar(getControl().getFilter(), getControl().getEngineCategories(), this.selectedListWidget);

        sidebar.javaFxSettingsManagerProperty().bind(getControl().javaFxSettingsManagerProperty());

        // set the default selection
        sidebar.setSelectedListWidget(Optional
                .ofNullable(getControl().getJavaFxSettingsManager())
                .map(JavaFxSettingsManager::getEnginesListType)
                .orElse(ListWidgetType.ICONS_LIST));

        // save changes to the list widget selection to the hard drive
        sidebar.selectedListWidgetProperty().addListener((observable, oldValue, newValue) -> {
            final JavaFxSettingsManager javaFxSettingsManager = getControl().getJavaFxSettingsManager();

            if (newValue != null) {
                javaFxSettingsManager.setEnginesListType(newValue);
                javaFxSettingsManager.save();
            }
        });

        sidebar.selectedEngineCategoryProperty().addListener((Observable invalidation) -> {
            final EngineCategoryDTO engineCategory = sidebar.getSelectedEngineCategory();

            if (engineCategory != null) {
                getControl().refreshEngineCategory(engineCategory);
            }
        });

        return new SimpleObjectProperty<>(sidebar);
    }

    @Override
    public ObjectExpression<Node> createContent() {
        final TabPane availableEngines = new TabPane();

        availableEngines.getStyleClass().add("rightPane");
        availableEngines.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        Bindings.bindContent(availableEngines.getTabs(), this.engineSubCategoryTabs);

        return new SimpleObjectProperty<>(availableEngines);
    }

    @Override
    public ObjectExpression<DetailsPanel> createDetailsPanel() {
        final EngineInformationPanel engineInformationPanel = new EngineInformationPanel();

        engineInformationPanel.engineDTOProperty().bind(getControl().engineDTOProperty());
        engineInformationPanel.engineProperty().bind(getControl().engineProperty());

        engineInformationPanel.setOnEngineInstall(getControl()::installEngine);
        engineInformationPanel.setOnEngineDelete(getControl()::deleteEngine);

        final DetailsPanel detailsPanel = new DetailsPanel();

        detailsPanel.titleProperty().bind(StringBindings
                .map(getControl().engineDTOProperty(), engine -> engine.getCategory() + " " + engine.getSubCategory()));
        detailsPanel.setContent(engineInformationPanel);

        detailsPanel.setOnClose(() -> {
            // TODO: deselect the selected engine inside the list widget
            getControl().setEngine(null);
            getControl().setEngineDTO(null);
        });

        detailsPanel.prefWidthProperty().bind(getControl().widthProperty().divide(3));

        return Bindings.when(Bindings.and(Bindings.isNotNull(getControl().engineProperty()), Bindings.isNotNull(getControl().engineDTOProperty())))
                .then(detailsPanel).otherwise(new SimpleObjectProperty<>());
    }
}
