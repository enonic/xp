package com.enonic.xp.app;

import java.time.Instant;
import java.util.Set;

import com.enonic.xp.config.Configuration;
import com.enonic.xp.util.Version;


public interface Application
{
    ApplicationKey getKey();

    Version getVersion();

    String getSystemVersion();

    String getMaxSystemVersion();

    String getMinSystemVersion();

    ClassLoader getClassLoader();

    Instant getModifiedTime();

    Set<String> getCapabilities();

    boolean isStarted();

    Configuration getConfig();

    boolean isSystem();

    /**
     * Whether the application is codeless: it owns its schema ({@code cms/cms.yaml}) without shipping any code, is always
     * started and can neither be started nor stopped.
     *
     * @return {@code true} for a codeless application
     */
    boolean isCodeless();
}
