package com.enonic.xp.app;

import java.time.Instant;
import java.util.Set;


import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.config.Configuration;
import com.enonic.xp.util.Version;

@PublicApi
public interface Application
{
    ApplicationKey getKey();

    Version getVersion();

    String getDisplayName();

    String getSystemVersion();

    String getMaxSystemVersion();

    String getMinSystemVersion();

    String getUrl();

    String getVendorName();

    String getVendorUrl();

    ClassLoader getClassLoader();

    Instant getModifiedTime();

    Set<String> getCapabilities();

    boolean isStarted();

    Configuration getConfig();

    boolean isSystem();
}
