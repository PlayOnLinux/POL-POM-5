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

package org.phoenicis.tools;

import org.phoenicis.configuration.PhoenicisGlobalConfiguration;
import org.phoenicis.tools.archive.Extractor;
import org.phoenicis.tools.archive.Tar;
import org.phoenicis.tools.archive.Zip;
import org.phoenicis.tools.checksum.ChecksumCalculator;
import org.phoenicis.tools.config.CompatibleConfigFileFormatFactory;
import org.phoenicis.tools.files.*;
import org.phoenicis.tools.http.Downloader;
import org.phoenicis.tools.lnk.LnkParser;
import org.phoenicis.tools.processes.ProcessUtils;
import org.phoenicis.tools.system.ArchitectureFetcher;
import org.phoenicis.tools.system.OperatingSystemFetcher;
import org.phoenicis.tools.system.ScreenManager;
import org.phoenicis.tools.system.GraphicsPropertiesFetcher;
import org.phoenicis.tools.system.SystemConfiguration;
import org.phoenicis.tools.system.opener.AutomaticOpener;
import org.phoenicis.tools.system.opener.Opener;
import org.phoenicis.tools.system.opener.OpenerProcessImplementation;
import org.phoenicis.tools.system.terminal.TerminalOpener;
import org.phoenicis.tools.win32.ExeAnalyser;
import org.phoenicis.win32.Win32Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;

@Configuration
@Import(SystemConfiguration.class)
public class ToolsConfiguration {
    @Autowired
    private Win32Configuration win32Configuration;

    @Autowired
    private SystemConfiguration systemConfiguration;

    @Autowired
    private PhoenicisGlobalConfiguration phoenicisGlobalConfiguration;

    @Bean
    public CompatibleConfigFileFormatFactory compatibleConfigFileFormatFactory() {
        return new CompatibleConfigFileFormatFactory(phoenicisGlobalConfiguration.objectMapper());
    }

    @Bean
    public FileAnalyser fileAnalyser() {
        return new FileAnalyser();
    }

    @Bean
    public FileUtilities fileUtilities() {
        return new FileUtilities();
    }

    @Bean
    public FileSizeUtilities fileSizeUtilities() {
        return new FileSizeUtilities();
    }

    @Bean
    public FileSearcher fileSearcher() {
        return new FileSearcher();
    }

    @Bean
    Tar tar() {
        return new Tar(fileUtilities());
    }

    @Bean
    Zip zip() {
        return new Zip();
    }

    @Bean
    public Extractor extractor() {
        return new Extractor(fileAnalyser(), tar(), zip());
    }

    @Bean
    public FileCopier fileCopier() {
        return new FileCopier();
    }

    @Bean
    public FreeSpaceFetcher freeSpaceFetcher() {
        return new FreeSpaceFetcher();
    }

    @Bean
    public Downloader downloader() {
        return new Downloader(fileSizeUtilities());
    }

    @Bean
    public ChecksumCalculator checksumCalculator() {
        return new ChecksumCalculator();
    }

    @Bean
    public OperatingSystemFetcher operatingSystemFetcher() {
        return systemConfiguration.operatingSystemFetcher();
    }

    @Bean
    public ArchitectureFetcher architectureFetcher() {
        return new ArchitectureFetcher(operatingSystemFetcher());
    }

    @Bean
    public ExeAnalyser exeAnalyser() {
        return new ExeAnalyser(win32Configuration.peReader());
    }

    @Bean
    public TerminalOpener terminalOpener() {
        return systemConfiguration.terminalOpener();
    }

    @Bean
    public ProcessUtils processUtils() {
        return new ProcessUtils();
    }

    @Bean
    @Lazy
    Opener linuxOpener() {
        return new OpenerProcessImplementation("xdg-open");
    }

    @Bean
    @Lazy
    Opener macOsOpener() {
        return new OpenerProcessImplementation("open");
    }

    @Bean
    public Opener opener() {
        return new AutomaticOpener(linuxOpener(), macOsOpener(), operatingSystemFetcher());
    }

    @Bean
    public LnkParser lnkParser() {
        return new LnkParser(new org.phoenicis.lnk.LnkParser());
    }

    @Bean
    public ScreenManager screenManager() {
        return new ScreenManager();
    }

    @Bean
    public GraphicsPropertiesFetcher graphicsPropertiesFetcher() {
        return new GraphicsPropertiesFetcher();
    }
}
