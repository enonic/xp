package com.enonic.wem.api.module;

import java.net.URL;
import java.util.Set;

import com.enonic.wem.api.Identity;
import com.enonic.wem.api.form.Form;

public interface Module
    extends Identity<ModuleKey, ModuleName>
{
    public ModuleKey getKey();

    public ModuleName getName();

    public ModuleVersion getVersion();

    public String getDisplayName();

    public String getUrl();

    public String getVendorName();

    public String getVendorUrl();

    public Form getConfig();

    public URL getResource( String path );

    public Set<String> getResourcePaths();
}
