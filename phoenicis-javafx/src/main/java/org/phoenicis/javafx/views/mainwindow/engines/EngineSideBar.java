package org.phoenicis.javafx.views.mainwindow.engines;

import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ToggleButton;
import org.phoenicis.engines.dto.EngineCategoryDTO;
import org.phoenicis.javafx.views.mainwindow.MainWindowView;
import org.phoenicis.javafx.views.mainwindow.ui.*;

import java.util.Optional;
import java.util.function.Consumer;

import static org.phoenicis.configuration.localisation.Localisation.translate;

/**
 * An instance of this class represents the left sidebar of the engines tab view.
 * This sidebar contains three items:
 * <ul>
 * <li>
 * A searchbar, which enables the user to search for an engine.
 * </li>
 * <li>
 * A button group containing a button for all known engine groups.
 * After pressing on one such button all engines belonging to the selected engine group are shown in the main window panel.
 * </li>
 * <li>
 * A button group containing buttons to filter for installed and uninstalled engines.
 * </li>
 * </ul>
 *
 * @author marc
 * @since 22.04.17
 */
public class EngineSideBar extends LeftSideBar {
    // the search bar used for filtering
    private SearchBox searchBar;

    // an optional button used to return to the last page
    private Button backButton;

    // the button group containing a button for all engine categories
    private LeftToggleGroup<EngineCategoryDTO> categoryView;

    // the button group containing a button to filter the engines for installed and uninstalled engines
    private LeftGroup installationFilterGroup;

    private CheckBox installedCheck;
    private CheckBox notInstalledCheck;

    // consumers called when an action inside the search bar has been performed
    private Consumer<String> onApplySearchTerm;
    private Runnable onSearchTermClear;

    // consumers called when a filter in the installation filter group has been activated
    private Consumer<Boolean> onApplyInstalledFilter;
    private Consumer<Boolean> onApplyUninstalledFilter;

    // consumer called when a category has been selected
    private Consumer<EngineCategoryDTO> onCategorySelection;

    /**
     * Constructor
     *
     * @param mainWindow The main window view in which this sidebar resides
     */
    public EngineSideBar(MainWindowView<EngineSideBar> mainWindow) {
        super(mainWindow);

        this.populateSearchBar();
        this.populateEngineCategories();
        this.populateInstallationFilters();

        this.showContent(Optional.empty());
    }

    public void showContent(Optional<MainWindowView.NavigationStep> lastNavigationStep) {
        if (!lastNavigationStep.isPresent()) {
            this.getChildren().setAll(this.searchBar, new LeftSpacer(), this.categoryView, new LeftSpacer(), this.installationFilterGroup);
        } else {
            this.backButton = new Button("Back");

            lastNavigationStep.get().getName().ifPresent(to -> this.backButton.setText(String.format("Back to %s", to)));

            this.backButton.setWrapText(true);
            this.backButton.setOnAction(event -> mainWindow.navigateToLast());

            this.getChildren().setAll(this.backButton, new LeftSpacer(), this.categoryView, new LeftSpacer(), this.installationFilterGroup);
        }
    }

    /**
     * This method takes an {@link ObservableList} of engine categories and binds it to the engrine categories button group
     *
     * @param engineCategories The list of engine categories
     */
    public void bindEngineCategories(ObservableList<EngineCategoryDTO> engineCategories) {
        Bindings.bindContent(categoryView.getElements(), engineCategories);
    }

    /**
     * This method populates the searchbar
     */
    private void populateSearchBar() {
        this.searchBar = new SearchBox(filterText -> onApplySearchTerm.accept(filterText), () -> onSearchTermClear.run());
    }

    /**
     * This method populates the button group showing all known engine categories
     */
    private void populateEngineCategories() {
        this.categoryView = LeftToggleGroup.create(translate("Engines"), this::createCategoryToggleButton);
    }

    /**
     * This method populates the button group containing buttons to filter for installed and not installed engines
     */
    private void populateInstallationFilters() {
        this.installedCheck = new LeftCheckBox(translate("Installed"));
        this.installedCheck.setSelected(true);
        this.installedCheck.selectedProperty()
                .addListener((observableValue, oldValue, newValue) -> onApplyInstalledFilter.accept(newValue));

        this.notInstalledCheck = new LeftCheckBox(translate("Not installed"));
        this.notInstalledCheck.setSelected(true);
        this.notInstalledCheck.selectedProperty()
                .addListener((observableValue, oldValue, newValue) -> onApplyUninstalledFilter.accept(newValue));

        this.installationFilterGroup = new LeftGroup(installedCheck, notInstalledCheck);
    }

    /**
     * This method creates a new toggle button for a given engine category.
     *
     * @param category The engine category, for which a new toggle button should be created
     * @return The created toggle button
     */
    private ToggleButton createCategoryToggleButton(EngineCategoryDTO category) {
        ToggleButton categoryButton = new LeftToggleButton(category.getName());

        categoryButton.setId(String.format("%sButton", category.getName().toLowerCase()));
        categoryButton.setOnAction(event -> onCategorySelection.accept(category));

        return categoryButton;
    }

    /**
     * This method selects the button belonging to the first engine category in the engine category button group.
     * If no engine category exists, this method will throw an {@link IllegalArgumentException}.
     *
     * @throws IllegalArgumentException
     */
    public void selectFirstEngineCategory() {
        this.categoryView.select(0);
    }

    /**
     * This method updates the consumer, that is called when an engines category gets selected
     *
     * @param onCategorySelection The new consumer to be called
     */
    public void setOnCategorySelection(Consumer<EngineCategoryDTO> onCategorySelection) {
        this.onCategorySelection = onCategorySelection;
    }

    public void setOnApplySearchTerm(Consumer<String> onApplySearchTerm) {
        this.onApplySearchTerm = onApplySearchTerm;
    }

    public void setOnSearchTermClear(Runnable onSearchTermClear) {
        this.onSearchTermClear = onSearchTermClear;
    }

    public void setOnApplyInstalledFilter(Consumer<Boolean> onApplyInstalledFilter) {
        this.onApplyInstalledFilter = onApplyInstalledFilter;
    }

    public void setOnApplyUninstalledFilter(Consumer<Boolean> onApplyUninstalledFilter) {
        this.onApplyUninstalledFilter = onApplyUninstalledFilter;
    }
}
