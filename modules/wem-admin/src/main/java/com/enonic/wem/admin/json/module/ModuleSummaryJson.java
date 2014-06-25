package com.enonic.wem.admin.json.module;

import java.time.Instant;

import org.osgi.framework.Bundle;

import com.enonic.wem.admin.json.ItemJson;
import com.enonic.wem.api.module.Module;

public class ModuleSummaryJson
    implements ItemJson
{
    final Module module;

    public ModuleSummaryJson( final Module module )
    {
        this.module = module;
    }

    public String getKey()
    {
        return module.getKey().toString();
    }

    public String getName()
    {
        return module.getName().toString();
    }

    public String getVersion()
    {
        return module.getVersion().toString();
    }

    public String getDisplayName()
    {
        return module.getDisplayName();
    }

    public String getUrl()
    {
        return module.getUrl();
    }

    public String getVendorName()
    {
        return module.getVendorName();
    }

    public String getVendorUrl()
    {
        return module.getVendorUrl();
    }

    public Instant getModifiedTime()
    {
        return Instant.ofEpochMilli( module.getBundle().getLastModified() );
    }

    public boolean isStarted()
    {
        return this.module.getBundle().getState() == Bundle.ACTIVE;
    }

    @Override
    public boolean getDeletable()
    {
        return false;
    }

    @Override
    public boolean getEditable()
    {
        return false;
    }
}
