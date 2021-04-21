package org.phoenicis.engines;

import org.phoenicis.scripts.wizard.SetupWizard;

import java.util.Map;

/**
 * interface which must be implemented by all engines in Javascript
 */
public interface Engine {

    /**
     * returns the local directory (e.g. ~/.Phoenicis/engines/wine/upstream-linux-x86/3.0)
     *
     * @param subCategory e.g. upstream-linux-x86
     * @param version e.g. 3.0
     * @return local directory
     */
    String getLocalDirectory(String subCategory, String version);

    /**
     * returns true if the engine is installed
     *
     * @param subCategory e.g. upstream-linux-x86
     * @param version e.g. 3.0
     * @return is engine version installed
     */
    boolean isInstalled(String subCategory, String version);

    /**
     * installs the given engine version
     *
     * @param subCategory e.g. upstream-linux-x86
     * @param version e.g. 3.0
     */
    void install(String subCategory, String version);

    /**
     * changes the engine version of the given container
     *
     * @param containerName the name of the container
     */
    void changeVersion(String containerName);

    /**
     * deletes the given engine version
     *
     * @param subCategory e.g. upstream-linux-x86
     * @param version e.g. 3.0
     */
    void delete(String subCategory, String version);

    /**
     * fetches the available versions
     *
     * @return content of JSON file (format like the file provided by webservice.wine.url)
     */
    String getAvailableVersions();

    /**
     * returns the current working container (i.e. name of container directory without full path)
     *
     * @return working container
     */
    String getWorkingContainer();

    /**
     * sets the working container (i.e. name of container directory without full path)
     *
     * @param workingContainer working container
     */
    void setWorkingContainer(String workingContainer);

    /**
     * returns path of a container
     *
     * @param containerName name of the container
     * @return path of container
     */
    String getContainerDirectory(String containerName);

    /**
     * creates a container
     *
     * @param subCategory e.g. upstream-linux-x86
     * @param version e.g. 3.0
     * @param containerName name of the container
     */
    void createContainer(String subCategory, String version, String containerName);

    /**
     * runs an executable with this engine
     *
     * @param executable executable
     * @param args program arguments
     * @param workingDir working directory
     * @param captureOutput true if output shall be captured
     * @param wait wait until run finished
     * @param userData engine specific data
     * @return output
     */
    String run(String executable, String[] args, String workingDir, boolean captureOutput, boolean wait,
            Map<String, String> userData);

    /**
     * returns the setup wizard
     *
     * @return setup wizard
     */
    SetupWizard getWizard();

    /**
     * sets the setup wizard
     *
     * @param wizard setup wizard
     */
    void setWizard(SetupWizard wizard);

    /**
     * Registers as verb as being "installed"
     *
     * @param verbId The ID of the verb
     * @param container The container where the verb has been installed
     */
    void registerVerb(String verbId, String container);

    /**
     * Checks whether a given verb has been registered as being "installed"
     *
     * @param verbId The ID of the verb
     * @param container The container where the verb has been installed
     * @return True if the verb has been registered as being "installed", false otherwise
     */
    boolean isVerbRegistered(String verbId, String container);
}
