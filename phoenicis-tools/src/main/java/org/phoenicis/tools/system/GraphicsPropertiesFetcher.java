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

package org.phoenicis.tools.system;

import org.phoenicis.configuration.security.Safe;
import org.phoenicis.tools.system.GraphicsProperties;
import org.phoenicis.tools.system.ArchitectureFetcher;
import org.phoenicis.tools.system.OperatingSystemFetcher;
import org.phoenicis.entities.Architecture;
import org.phoenicis.entities.OperatingSystem;

import java.util.ArrayList;
import java.util.Arrays;
import static org.phoenicis.configuration.localisation.Localisation.tr;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.vulkan.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.glfw.GLFWVulkan.*;

// Code from LWJGL tutorial
@Safe
/**
 * This class fetch the required properties to fill the class GraphicsProperties,
 * using LWJGL to create a dummy window and context in order to access OpenGL and Vulkan properties.
 */
public class GraphicsPropertiesFetcher {
    private long window = NULL;

    /**
     * Create an invisible glfx window and context, from which infos will be retrieved
     */
    private void init(GraphicsProperties graphicsProperties) {
        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit())
            throw new IllegalStateException(tr("Unable to initialize GLFW for testing graphic card capabilities"));

        // We will now fetch maximum supported OpenGL core context version (3.2 -> 4.6)
        ArrayList<ArrayList<Integer>> openglCoreVersion = new ArrayList<ArrayList<Integer>>(); // Versions that
                                                                                               // distinguish core and
                                                                                               // compatibility

        openglCoreVersion.add(new ArrayList<Integer>(Arrays.asList(6, 5, 4, 3, 2, 1, 0))); // OpenGL 4.x
        openglCoreVersion.add(new ArrayList<Integer>(Arrays.asList(3, 2))); // OpenGL 3.x

        // We run through the versions than should provide a core context
        boolean found = false;
        for (int i = 0; i < openglCoreVersion.size(); ++i) {
            if (!found) {
                for (int j = 0; i < openglCoreVersion.get(i).size(); ++j) {
                    glfwDefaultWindowHints();
                    glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
                    glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, -i + 4);
                    glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, openglCoreVersion.get(i).get(j));
                    glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);

                    this.window = glfwCreateWindow(300, 300, "Test Window", NULL, NULL);

                    if (this.window != NULL) {
                        // We found a working core context
                        found = true;
                        graphicsProperties.setOpenGLCoreVersion(String.valueOf(-i + 4) + "."
                                + String.valueOf(openglCoreVersion.get(i).get(j)));
                        glfwFreeCallbacks(this.window);
                        glfwDestroyWindow(this.window);
                        this.window = NULL;
                        break;
                    }

                    // No core context found, the context is then 3.1 or less, see fetchVendorRendererOpenGLVersion
                }
            }
        }

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);

        this.window = glfwCreateWindow(300, 300, "Test Window", NULL, NULL);
        if (this.window == NULL)
            throw new IllegalStateException(
                    tr("Failed to create the GLFW window for testing graphic card capabilities"));
    }

    /**
     * Destroy glfw window and context
     */
    private void terminate() {
        glfwFreeCallbacks(this.window);
        glfwDestroyWindow(this.window);

        glfwTerminate();
        glfwSetErrorCallback(null).free();
        this.window = NULL;
    }

    /**
     * Fetch graphics card vendor and OpenGL version
     */
    private void fetchVendorRendererOpenGLVersion(GraphicsProperties graphicsProperties) {
        // Allow LWJGL to connect with the glfw OpenGL context and to use gl* function
        glfwMakeContextCurrent(this.window);
        GL.createCapabilities();

        graphicsProperties.setVendor(glGetString(GL_VENDOR));
        graphicsProperties.setRenderer(glGetString(GL_RENDERER));
        graphicsProperties.setOpenGLVersion(glGetString(GL_VERSION)); // The version can be inferior to
                                                                      // openglCoreVersion
                                                                      // If the compatibiltity context is not available
                                                                      // for large OpenGL version
        graphicsProperties.setOpenGLVersion(graphicsProperties.getOpenGLVersion().substring(0,
                graphicsProperties.getOpenGLVersion().indexOf(' '))); // We only take to version number
    }

    /**
     * Fetch Vulkan version
     */
    private void fetchVulkanVersion(GraphicsProperties graphicsProperties) {
        if (!glfwVulkanSupported()) {
            return;
        }

        // Gets normally maximum Vulkan version fully supported
        int version = VK.getInstanceVersionSupported();

        // Convert the uint32 into a readable String (source: vulkaninfo source code)
        graphicsProperties.setVulkanVersion(String.valueOf(version >> 22) + "." +
                String.valueOf((version >> 12) & 0x3ff) + "." +
                String.valueOf(version & 0xfff));
    }

    /**
     * Fetch the current graphics properties (vendor, OpenGL version, Vulkan version) of the system
     * 
     * @return The current graphics properties inside a GraphicsProperties object
     */
    public GraphicsProperties getProperties() {
        final GraphicsProperties graphicsProperties = new GraphicsProperties();

        final OperatingSystemFetcher operatingSystemFetcher = new OperatingSystemFetcher();
        OperatingSystem os = operatingSystemFetcher.fetchCurrentOperationSystem();

        final ArchitectureFetcher architectureFetcher = new ArchitectureFetcher(operatingSystemFetcher);
        Architecture arch = architectureFetcher.fetchCurrentArchitecture();

        if (arch == Architecture.AMD64 && (os == OperatingSystem.LINUX || os == OperatingSystem.MACOSX)) {
            init(graphicsProperties);

            fetchVendorRendererOpenGLVersion(graphicsProperties);
            fetchVulkanVersion(graphicsProperties);

            terminate();
        }

        return graphicsProperties;
    }
}
