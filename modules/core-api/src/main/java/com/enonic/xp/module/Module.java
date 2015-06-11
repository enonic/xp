package com.enonic.xp.module;

import java.net.URL;
import java.time.Instant;
import java.util.Set;

import org.osgi.framework.Bundle;

import com.google.common.annotations.Beta;

@Beta
public interface Module
{
    ModuleKey getKey();

    ModuleVersion getVersion();

    String getDisplayName();

    String getSystemVersion();

    String getMaxSystemVersion();

    String getMinSystemVersion();

    String getUrl();

    String getVendorName();

    String getVendorUrl();

    URL getResource( String path );

    Set<String> getResourcePaths();

    Bundle getBundle();

    Instant getModifiedTime();

    boolean isStarted();

    ClassLoader getClassLoader();

    void checkIfStarted();
}
