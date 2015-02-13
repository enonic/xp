package com.enonic.wem.api.module;

import java.net.URL;
import java.util.Set;

import org.osgi.framework.Bundle;

import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.schema.mixin.MixinNames;

public interface Module
{
    public ModuleKey getKey();

    public ModuleVersion getVersion();

    public String getDisplayName();

    public String getMaxSystemVersion();

    public String getMinSystemVersion();

    public String getUrl();

    public String getVendorName();

    public String getVendorUrl();

    public Form getConfig();

    public URL getResource( String path );

    public Set<String> getResourcePaths();

    public Bundle getBundle();

    public MixinNames getMetaSteps();

    // public <T> T getValue( Class<T> type, Callable<T> creator );
}
