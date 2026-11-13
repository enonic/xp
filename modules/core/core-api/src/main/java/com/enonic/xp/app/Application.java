package com.enonic.xp.app;

import java.time.Instant;
import java.util.Set;

import com.enonic.xp.config.Configuration;
import com.enonic.xp.util.Version;

/**
 * An application installed in XP.
 */
public interface Application
{
    /**
     * Returns the key identifying the application.
     *
     * @return the application key
     */
    ApplicationKey getKey();

    /**
     * Returns the version of the application.
     *
     * @return the application version
     */
    Version getVersion();

    /**
     * Returns the range of XP versions the application declares itself compatible with, for example {@code [7.0.0,8.0.0)}.
     *
     * @return the system version range, or {@code null} when the application declares none
     */
    String getSystemVersion();

    /**
     * Returns the upper bound of the XP version range the application declares itself compatible with.
     *
     * @return the maximum system version, or {@code null} when the range has no upper bound or the application declares none
     */
    String getMaxSystemVersion();

    /**
     * Returns the lower bound of the XP version range the application declares itself compatible with.
     *
     * @return the minimum system version, or {@code null} when the application declares none
     */
    String getMinSystemVersion();

    /**
     * Returns the class loader loading the classes and resources of the application.
     *
     * @return the application class loader
     */
    ClassLoader getClassLoader();

    /**
     * Returns the time the application was last installed or updated.
     *
     * @return the modification time
     */
    Instant getModifiedTime();

    /**
     * Returns the capabilities the application declares in its manifest.
     *
     * @return the declared capabilities, empty when the application declares none
     */
    Set<String> getCapabilities();

    /**
     * Returns whether the application is started: only a started application serves its code and resources.
     *
     * @return {@code true} when the application is started
     */
    boolean isStarted();

    /**
     * Returns the configuration of the application, read from its configuration file.
     *
     * @return the application configuration
     */
    Configuration getConfig();

    /**
     * Returns whether the application is a system application shipped with XP. The schema and the application descriptor
     * of a system application cannot be modified through the schema and application descriptor services.
     *
     * @return {@code true} for a system application
     */
    boolean isSystem();

    /**
     * Returns whether the application is a schema application: one that owns a CMS schema, with content types, components
     * and other schema resources served through the schema service.
     *
     * @return {@code true} for a schema application
     */
    boolean isSchema();

    /**
     * Returns whether the application is a local application: one deployed from the deploy directory of this node only.
     * The schema and the application descriptor of a local application cannot be modified through the schema and application
     * descriptor services.
     *
     * @return {@code true} for a local application
     */
    boolean isLocal();
}