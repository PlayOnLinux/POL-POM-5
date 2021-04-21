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

@Safe
/**
 * This class contains some information about the graphics capabilities of the GPU:
 * the GPU vendor, name (renderer), the OpenGL and Vulkan version
 */
public class GraphicsProperties {
    private String vendor;
    private String renderer;
    private String openglVersion;
    private String openglCoreVersion;
    private String vulkanVersion;

    public GraphicsProperties() {
        this.vendor = "Unknown";
        this.renderer = "Unknown";
        this.openglVersion = "Unsupported";
        this.openglCoreVersion = "Unsupported";
        this.vulkanVersion = "Unsupported";
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public void setRenderer(String renderer) {
        this.renderer = renderer;
    }

    public void setOpenGLVersion(String openglVersion) {
        this.openglVersion = openglVersion;
    }

    public void setOpenGLCoreVersion(String openglCoreVersion) {
        this.openglCoreVersion = openglCoreVersion;
    }

    public void setVulkanVersion(String vulkanVersion) {
        this.vulkanVersion = vulkanVersion;
    }

    public String getVendor() {
        return this.vendor;
    }

    public String getRenderer() {
        return this.renderer;
    }

    public String getOpenGLVersion() {
        return this.openglVersion;
    }

    public String getOpenGLCoreVersion() {
        return this.openglCoreVersion;
    }

    public String getVulkanVersion() {
        return this.vulkanVersion;
    }
}
