package com.enonic.xp.admin.impl.json.module;

import java.time.Instant;
import java.util.List;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.admin.impl.json.ItemJson;
import com.enonic.xp.form.FormJson;
import com.enonic.xp.module.Module;
import com.enonic.xp.schema.mixin.MixinName;
import com.enonic.xp.site.SiteDescriptor;

public class ModuleJson
    implements ItemJson
{
    final Module module;

    private final FormJson config;

    private final ImmutableList<String> metaStepMixinNames;

    public ModuleJson( final Module module, final SiteDescriptor siteDescriptor )
    {
        this.module = module;
        this.config = siteDescriptor != null && siteDescriptor.getForm() != null ? new FormJson( siteDescriptor.getForm() ) : null;
        ImmutableList.Builder<String> mixinNamesBuilder = new ImmutableList.Builder<>();
        if ( siteDescriptor != null && siteDescriptor.getMetaSteps() != null )
        {
            for ( MixinName mixinName : siteDescriptor.getMetaSteps() )
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
        return this.module.getModifiedTime();
    }

    public String getState()
    {
        return this.module.isStarted() ? "started" : "stopped";
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
