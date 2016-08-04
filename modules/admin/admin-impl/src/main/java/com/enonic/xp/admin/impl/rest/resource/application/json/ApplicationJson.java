package com.enonic.xp.admin.impl.rest.resource.application.json;

import java.time.Instant;
import java.util.List;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.admin.impl.json.ItemJson;
import com.enonic.xp.admin.impl.json.form.FormJson;
import com.enonic.xp.admin.impl.rest.resource.application.ApplicationIconUrlResolver;
import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationDescriptor;
import com.enonic.xp.auth.AuthDescriptor;
import com.enonic.xp.schema.mixin.MixinName;
import com.enonic.xp.site.SiteDescriptor;

public class ApplicationJson
    implements ItemJson
{
    private final Application application;

    private final ApplicationDescriptor applicationDescriptor;

    private final boolean local;

    private final FormJson config;

    private final FormJson authConfig;

    private final ImmutableList<String> metaStepMixinNames;

    private final String iconUrl;

    public ApplicationJson( final Application application, final boolean local, final ApplicationDescriptor applicationDescriptor,
                            final SiteDescriptor siteDescriptor, final AuthDescriptor authDescriptor,
                            final ApplicationIconUrlResolver iconUrlResolver )
    {
        this.application = application;
        this.applicationDescriptor = applicationDescriptor;
        this.local = local;
        this.config = siteDescriptor != null && siteDescriptor.getForm() != null ? new FormJson( siteDescriptor.getForm() ) : null;
        this.authConfig = authDescriptor != null && authDescriptor.getConfig() != null ? new FormJson( authDescriptor.getConfig() ) : null;
        ImmutableList.Builder<String> mixinNamesBuilder = new ImmutableList.Builder<>();
        if ( siteDescriptor != null && siteDescriptor.getMetaSteps() != null )
        {
            for ( MixinName mixinName : siteDescriptor.getMetaSteps() )
            {
                mixinNamesBuilder.add( mixinName.toString() );
            }
        }
        this.metaStepMixinNames = mixinNamesBuilder.build();
        this.iconUrl = iconUrlResolver.resolve( application.getKey(), applicationDescriptor );
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

    public boolean getLocal()
    {
        return local;
    }

    public FormJson getConfig()
    {
        return config;
    }

    public FormJson getAuthConfig()
    {
        return authConfig;
    }

    public List<String> getMetaSteps()
    {
        return metaStepMixinNames;
    }

    public String getDescription()
    {
        return applicationDescriptor == null ? "" : applicationDescriptor.getDescription();
    }

    public String getIconUrl()
    {
        return iconUrl;
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
