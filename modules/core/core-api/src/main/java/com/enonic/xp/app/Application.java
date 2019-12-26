package com.enonic.xp.app;

import java.net.URL;
import java.time.Instant;
import java.util.Set;

import org.osgi.framework.Bundle;
import org.osgi.framework.Version;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.config.Configuration;

@PublicApi
public interface Application
{
    ApplicationKey getKey();

    Version getVersion();

    String getDisplayName();

    String getSystemVersion();

    String getMaxSystemVersion();

    String getMinSystemVersion();

    boolean includesSystemVersion( final Version version );

    String getUrl();

    String getVendorName();

    String getVendorUrl();

    Bundle getBundle();

    ClassLoader getClassLoader();

    Instant getModifiedTime();

    Set<String> getCapabilities();

    boolean isStarted();

    Set<String> getFiles();

    URL resolveFile( String path );

    Configuration getConfig();

    boolean isSystem();
}
