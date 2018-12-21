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

package org.phoenicis.javafx.views.mainwindow.installations;

import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import org.phoenicis.javafx.collections.ExpandedList;
import org.phoenicis.javafx.collections.MappedList;
import org.phoenicis.javafx.components.common.widgets.control.CombinedListWidget;
import org.phoenicis.javafx.settings.JavaFxSettingsManager;
import org.phoenicis.javafx.views.common.ThemeManager;
import org.phoenicis.javafx.components.common.widgets.utils.ListWidgetElement;
import org.phoenicis.javafx.views.mainwindow.installations.dto.InstallationCategoryDTO;
import org.phoenicis.javafx.views.mainwindow.installations.dto.InstallationDTO;
import org.phoenicis.javafx.views.mainwindow.ui.MainWindowView;

import java.util.Comparator;
import java.util.List;

import static org.phoenicis.configuration.localisation.Localisation.tr;

/**
 * The "Installations" tab shows the currently active installations.
 * <p>
 * This includes applications as well as engines.
 */
public class InstallationsView extends MainWindowView<InstallationsSidebar> {
    private final InstallationsFilter filter;
    private final JavaFxSettingsManager javaFxSettingsManager;

    private final ObservableList<InstallationCategoryDTO> categories;

    private CombinedListWidget<InstallationDTO> activeInstallations;

    private Runnable onInstallationAdded;

    /**
     * constructor
     *
     * @param themeManager
     * @param javaFxSettingsManager
     */
    public InstallationsView(ThemeManager themeManager, JavaFxSettingsManager javaFxSettingsManager) {
        super(tr("Installations"), themeManager);

        this.javaFxSettingsManager = javaFxSettingsManager;
        this.filter = new InstallationsFilter();
        this.categories = FXCollections.observableArrayList();

        this.getStyleClass().add("mainWindowScene");

        this.activeInstallations = createInstallationListWidget();

        filter.selectedInstallationCategoryProperty().addListener((Observable invalidation) -> closeDetailsView());

        setSidebar(createInstallationsSidebar());
        setCenter(activeInstallations);
    }

    private CombinedListWidget<InstallationDTO> createInstallationListWidget() {
        final FilteredList<InstallationDTO> filteredInstallations = new ExpandedList<>(
                categories.sorted(Comparator.comparing(InstallationCategoryDTO::getName)),
                InstallationCategoryDTO::getInstallations)
                        .filtered(filter::filter);

        filteredInstallations.predicateProperty().bind(
                Bindings.createObjectBinding(() -> filter::filter,
                        filter.searchTermProperty(), filter.selectedInstallationCategoryProperty()));

        final SortedList<InstallationDTO> sortedInstallations = filteredInstallations
                .sorted(Comparator.comparing(InstallationDTO::getName));

        final ObservableList<ListWidgetElement<InstallationDTO>> listWidgetEntries = new MappedList<>(
                sortedInstallations,
                ListWidgetElement::create);

        final CombinedListWidget<InstallationDTO> combinedListWidget = new CombinedListWidget<>(listWidgetEntries);

        combinedListWidget.selectedElementProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                showInstallationDetails(newValue.getItem());
            }
        });

        return combinedListWidget;
    }

    private InstallationsSidebar createInstallationsSidebar() {
        final SortedList<InstallationCategoryDTO> sortedCategories = categories
                .sorted(Comparator.comparing(InstallationCategoryDTO::getName));

        return new InstallationsSidebar(filter, javaFxSettingsManager, sortedCategories, activeInstallations);
    }

    /**
     * shows the given installations
     *
     * @param categories
     */
    private void populate(List<InstallationCategoryDTO> categories) {
        Platform.runLater(() -> {
            this.categories.setAll(categories);

            closeDetailsView();
            setCenter(activeInstallations);
        });
    }

    /**
     * shows details of the given installation
     *
     * @param installationDTO
     */
    private void showInstallationDetails(InstallationDTO installationDTO) {
        final InstallationsPanel installationsPanel = new InstallationsPanel();

        installationsPanel.setOnClose(this::closeDetailsView);
        installationsPanel.setInstallationDTO(installationDTO);
        installationsPanel.prefWidthProperty().bind(this.getTabPane().widthProperty().divide(2));

        showDetailsView(installationsPanel);
    }

    /**
     * adds new installation
     *
     * @param installationDTO new installation
     */
    public void addInstallation(InstallationDTO installationDTO) {
        populate(new InstallationsUtils().addInstallationToList(categories, installationDTO));

        Platform.runLater(() -> activeInstallations.select(installationDTO));

        onInstallationAdded.run();
    }

    /**
     * removes installation (if it exists)
     *
     * @param installationDTO installation to be removed
     */
    public void removeInstallation(InstallationDTO installationDTO) {
        populate(new InstallationsUtils().removeInstallationFromList(categories, installationDTO));
    }

    /**
     * sets Runnable which is executed whenever a new installation is added
     *
     * @param onInstallationAdded
     */
    public void setOnInstallationAdded(Runnable onInstallationAdded) {
        this.onInstallationAdded = onInstallationAdded;
    }

}
