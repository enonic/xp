package com.enonic.xp.admin.impl.json.application;

import java.time.Instant;
import java.util.List;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.admin.impl.json.ItemJson;
import com.enonic.xp.app.Application;
import com.enonic.xp.form.FormJson;
import com.enonic.xp.schema.mixin.MixinName;
import com.enonic.xp.site.SiteDescriptor;

public class ApplicationJson
    implements ItemJson
{
    final Application application;

    private final FormJson config;

    private final ImmutableList<String> metaStepMixinNames;

    public ApplicationJson( final Application application, final SiteDescriptor siteDescriptor )
    {
        this.application = application;
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
        return application.getKey().toString();
    }

    public String getVersion()
    {
        return application.getVersion().toString();
    }

    public String getDisplayName()
    {
        return application.getDisplayName();
    }

    public String getMaxSystemVersion()
    {
        return application.getMaxSystemVersion();
    }

    public String getMinSystemVersion()
    {
        return application.getMinSystemVersion();
    }

    public String getUrl()
    {
        return application.getUrl();
    }

    public String getVendorName()
    {
        return application.getVendorName();
    }

    public String getVendorUrl()
    {
        return application.getVendorUrl();
    }

    public Instant getModifiedTime()
    {
        return this.application.getModifiedTime();
    }

    public String getState()
    {
        return this.application.isStarted() ? "started" : "stopped";
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
