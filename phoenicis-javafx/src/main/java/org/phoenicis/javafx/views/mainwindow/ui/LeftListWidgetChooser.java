package org.phoenicis.javafx.views.mainwindow.ui;

import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import org.phoenicis.javafx.views.common.widget.CombinedListWidget;
import org.phoenicis.javafx.views.common.widget.ListWidgetType;

import javax.swing.*;

/**
 * Created by marc on 15.05.17.
 */
public class LeftListWidgetChooser<E> extends HBox {
    private ToggleGroup toggleGroup;

    private ToggleButton iconsListButton;
    private ToggleButton compactListButton;

    public LeftListWidgetChooser(CombinedListWidget<E> listWidget) {
        super();

        this.getStyleClass().add("listChooser");

        this.toggleGroup = new ToggleGroup();

        this.iconsListButton = new ToggleButton();
        this.iconsListButton.setToggleGroup(toggleGroup);
        this.iconsListButton.getStyleClass().addAll("listIcon", "iconsList");
        this.iconsListButton.setOnAction(event -> listWidget.showList(ListWidgetType.ICONS_LIST));

        this.compactListButton = new ToggleButton();
        this.compactListButton.setToggleGroup(toggleGroup);
        this.compactListButton.getStyleClass().addAll("listIcon", "compactList");
        this.compactListButton.setOnAction(event -> listWidget.showList(ListWidgetType.COMPACT_LIST));

        this.iconsListButton.setSelected(true);

        this.getChildren().setAll(iconsListButton, compactListButton);
    }
}
