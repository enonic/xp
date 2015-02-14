package com.enonic.xp.admin.impl.json.module;

import java.time.Instant;
import java.util.List;

import org.osgi.framework.Bundle;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.admin.impl.json.ItemJson;
import com.enonic.wem.api.form.FormJson;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.schema.mixin.MixinName;

public class ModuleJson
    implements ItemJson
{
    final Module module;

    private final FormJson config;

    private final ImmutableList<String> metaStepMixinNames;

    public ModuleJson( final Module module )
    {
        this.module = module;
        this.config = module.getConfig() != null ? new FormJson( module.getConfig() ) : null;
        ImmutableList.Builder<String> mixinNamesBuilder = new ImmutableList.Builder<>();
        if ( this.module.getMetaSteps() != null )
        {
            for ( MixinName mixinName : this.module.getMetaSteps() )
            {
                mixinNamesBuilder.add( mixinName.toString() );
            }
        }
        this.metaStepMixinNames = mixinNamesBuilder.build();
    }

    public String getKey()
    {
        return module.getKey().toString();
    }

    public String getVersion()
    {
        return module.getVersion().toString();
    }

    public String getDisplayName()
    {
        return module.getDisplayName();
    }

    public String getMaxSystemVersion()
    {
        return module.getMaxSystemVersion();
    }

    public String getMinSystemVersion()
    {
        return module.getMinSystemVersion();
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

    public String getState()
    {
        return ( this.module.getBundle().getState() == Bundle.ACTIVE ) ? "started" : "stopped";
    }

    public FormJson getConfig()
    {
        return config;
    }

    public List<String> getMetaSteps()
    {
        return metaStepMixinNames;
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
