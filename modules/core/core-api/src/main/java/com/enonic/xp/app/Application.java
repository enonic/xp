package com.enonic.xp.app;

import java.time.Instant;
import java.util.List;

import org.osgi.framework.Bundle;
import org.osgi.framework.Version;

import com.google.common.annotations.Beta;

@Beta
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

    Bundle getBundle();

    Instant getModifiedTime();

    List<String> getSourcePaths();

    boolean isStarted();

    boolean isApplication();

    boolean isSystem();
}
