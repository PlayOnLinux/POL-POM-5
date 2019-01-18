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

package org.phoenicis.javafx.views.mainwindow.settings;

import javafx.beans.Observable;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.phoenicis.javafx.components.setting.control.RepositoriesPanel;
import org.phoenicis.javafx.components.setting.control.SettingsSidebar;
import org.phoenicis.javafx.components.setting.utils.SettingsSidebarItem;
import org.phoenicis.javafx.settings.JavaFxSettingsManager;
import org.phoenicis.javafx.views.common.ThemeManager;
import org.phoenicis.javafx.views.mainwindow.ui.MainWindowView;
import org.phoenicis.repository.RepositoryLocationLoader;
import org.phoenicis.repository.RepositoryManager;
import org.phoenicis.repository.location.RepositoryLocation;
import org.phoenicis.repository.types.Repository;
import org.phoenicis.settings.SettingsManager;
import org.phoenicis.tools.system.opener.Opener;

import static org.phoenicis.configuration.localisation.Localisation.tr;

public class SettingsView extends MainWindowView<SettingsSidebar> {
    private final String applicationName;
    private final String applicationVersion;
    private final String applicationGitRevision;
    private final String applicationBuildTimestamp;
    private final Opener opener;

    private final RepositoryLocationLoader repositoryLocationLoader;

    private SettingsManager settingsManager;
    private JavaFxSettingsManager javaFxSettingsManager;
    private RepositoryManager repositoryManager;

    private ObservableList<SettingsSidebarItem> settingsItems;

    public SettingsView(ThemeManager themeManager, String applicationName, String applicationVersion,
            String applicationGitRevision, String applicationBuildTimestamp, Opener opener,
            SettingsManager settingsManager, RepositoryLocationLoader repositoryLocationLoader,
            JavaFxSettingsManager javaFxSettingsManager, RepositoryManager repositoryManager) {
        super(tr("Settings"), themeManager);
        this.applicationName = applicationName;
        this.applicationVersion = applicationVersion;
        this.applicationGitRevision = applicationGitRevision;
        this.applicationBuildTimestamp = applicationBuildTimestamp;
        this.opener = opener;
        this.settingsManager = settingsManager;
        this.repositoryLocationLoader = repositoryLocationLoader;
        this.javaFxSettingsManager = javaFxSettingsManager;
        this.repositoryManager = repositoryManager;

        this.initializeSettingsItems();

        this.sidebar = createSidebar();

        this.setSidebar(sidebar);

    }

    private SettingsSidebar createSidebar() {
        final SettingsSidebar sidebar = new SettingsSidebar(this.settingsItems);

        sidebar.selectedItemProperty()
                .addListener((Observable invalidation) -> setCenter(sidebar.getSelectedItem().getPanel()));

        return sidebar;
    }

    private void initializeSettingsItems() {
        AboutPanel.ApplicationBuildInformation buildInformation = new AboutPanel.ApplicationBuildInformation(
                this.applicationName, this.applicationVersion, this.applicationGitRevision,
                this.applicationBuildTimestamp);

        ObservableList<RepositoryLocation<? extends Repository>> repositoryLocations = FXCollections
                .observableArrayList(settingsManager.loadRepositoryLocations());

        repositoryLocations
                .addListener((Observable invalidation) -> System.out.println(repositoryLocations.toString()));

        this.settingsItems = FXCollections.observableArrayList(
                new SettingsSidebarItem(
                        new UserInterfacePanel(this.javaFxSettingsManager, this.themeManager),
                        "userInterfaceButton", tr("User Interface")),
                new SettingsSidebarItem(
                        new RepositoriesPanel(repositoryLocationLoader, repositoryLocations,
                                new SimpleObjectProperty<>()),
                        "repositoriesButton", tr("Repositories")),
                new SettingsSidebarItem(new FileAssociationsPanel(), "settingsButton",
                        tr("File Associations")),
                new SettingsSidebarItem(new NetworkPanel(), "networkButton", tr("Network")),
                new SettingsSidebarItem(new AboutPanel(buildInformation, this.opener), "aboutButton",
                        tr("About")));
    }
}
