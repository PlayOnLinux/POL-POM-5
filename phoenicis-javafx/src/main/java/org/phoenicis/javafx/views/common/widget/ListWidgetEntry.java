package org.phoenicis.javafx.views.common.widget;

import javafx.scene.effect.ColorAdjust;
import org.phoenicis.apps.dto.ApplicationDTO;
import org.phoenicis.containers.dto.ContainerDTO;
import org.phoenicis.engines.dto.EngineVersionDTO;
import org.phoenicis.javafx.views.mainwindow.containers.ContainerSideBar;
import org.phoenicis.library.dto.ShortcutDTO;

import javax.swing.text.html.Option;
import java.net.URI;
import java.util.List;
import java.util.Optional;

/**
 * Created by marc on 15.05.17.
 */
public class ListWidgetEntry<E> {
    private E item;

    private URI defaultIconUri;
    private Optional<URI> iconUri;

    private String title;
    private Optional<List<String>> additionalInformation;
    private Optional<List<String>> detailedInformation;

    public ListWidgetEntry(E item, Optional<URI> iconUri, URI defaultIconUri, String title,
            Optional<List<String>> additionalInformation, Optional<List<String>> detailedInformation) {
        super();

        this.item = item;

        this.defaultIconUri = defaultIconUri;
        this.iconUri = iconUri;

        this.title = title;
        this.additionalInformation = additionalInformation;
        this.detailedInformation = detailedInformation;
    }

    public static ListWidgetEntry<ApplicationDTO> create(ApplicationDTO application) {
        Optional<URI> iconPath = Optional.empty();
        if (!application.getMiniatures().isEmpty()) {
            iconPath = Optional.of(application.getMiniatures().get(0));
        }

        return new ListWidgetEntry<ApplicationDTO>(application, iconPath, StaticMiniature.DEFAULT_MINIATURE,
                application.getName(), Optional.empty(), Optional.empty());
    }

    public static ListWidgetEntry<ContainerDTO> create(ContainerDTO container) {
        return new ListWidgetEntry<ContainerDTO>(container, Optional.empty(), StaticMiniature.CONTAINER_MINIATURE,
                container.getName(), Optional.empty(), Optional.empty());
    }

    public static ListWidgetEntry<ShortcutDTO> create(ShortcutDTO shortcut) {
        return new ListWidgetEntry<ShortcutDTO>(shortcut, Optional.ofNullable(shortcut.getMiniature()),
                StaticMiniature.DEFAULT_MINIATURE, shortcut.getName(), Optional.empty(), Optional.empty());
    }

    public static ListWidgetEntry<EngineVersionDTO> create(EngineVersionDTO engineVersion, boolean installed) {
        ListWidgetEntry<EngineVersionDTO> result = new ListWidgetEntry<EngineVersionDTO>(engineVersion,
                Optional.empty(), StaticMiniature.WINE_MINIATURE, engineVersion.getVersion(), Optional.empty(),
                Optional.empty());

        //        if (!installed) {
        //            ColorAdjust grayscale = new ColorAdjust();
        //            grayscale.setSaturation(-1);
        //            result.setEffect(grayscale);
        //        }

        return result;
    }

    public E getItem() {
        return this.item;
    }

    public URI getIconUri() {
        return this.iconUri.orElse(defaultIconUri);
    }

    public String getTitle() {
        return this.title;
    }

    public Optional<List<String>> getAdditionalInformation() {
        return this.additionalInformation;
    }

    public Optional<List<String>> getDetailedInformation() {
        return this.detailedInformation;
    }
}
