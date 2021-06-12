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

package org.phoenicis.javafx;

import com.github.jankroken.commandline.CommandLineParser;
import com.github.jankroken.commandline.OptionStyle;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class JavaFXApplication extends Application {
    private final static Logger LOGGER = LoggerFactory.getLogger(JavaFXApplication.class);

    public static void main(String[] args) {
        try {
            Application.launch(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("views/common/phoenicis.png")));
        primaryStage.setTitle("Phoenicis");
        loadFonts();

        try {
            final List<String> parameters = getParameters().getRaw();
            String[] parametersArray = new String[parameters.size()];
            parametersArray = parameters.toArray(parametersArray);
            final CLIController arguments = CommandLineParser.parse(CLIController.class,
                    parametersArray, OptionStyle.SIMPLE);

            arguments.close();
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadFonts() {
        Font.loadFont(getClass().getResource("views/common/mavenpro/MavenPro-Medium.ttf").toExternalForm(), 12);
        Font.loadFont(getClass().getResource("views/common/roboto/Roboto-Medium.ttf").toExternalForm(), 12);
        Font.loadFont(getClass().getResource("views/common/roboto/Roboto-Light.ttf").toExternalForm(), 12);
        Font.loadFont(getClass().getResource("views/common/roboto/Roboto-Bold.ttf").toExternalForm(), 12);
    }

}
