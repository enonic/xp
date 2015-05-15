package com.enonic.xp.module;

import java.net.URL;
import java.time.Instant;
import java.util.Set;

import org.osgi.framework.Bundle;

import com.google.common.annotations.Beta;

import com.enonic.xp.form.Form;
import com.enonic.xp.schema.mixin.MixinNames;

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

    Form getConfig();

    URL getResource( String path );

    Set<String> getResourcePaths();

    Bundle getBundle();

    MixinNames getMetaSteps();

    Instant getModifiedTime();

    boolean isStarted();

    ClassLoader getClassLoader();
}
