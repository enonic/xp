package com.enonic.xp.admin.impl.rest.resource.application.json;

import java.time.Instant;
import java.util.List;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.admin.impl.json.ItemJson;
import com.enonic.xp.admin.impl.json.form.FormJson;
import com.enonic.xp.admin.impl.rest.resource.application.ApplicationIconUrlResolver;
import com.enonic.xp.admin.impl.rest.resource.schema.content.LocaleMessageResolver;
import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationDescriptor;
import com.enonic.xp.auth.AuthDescriptor;
import com.enonic.xp.schema.xdata.XDataName;
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

    public ApplicationJson( final Builder builder )
    {
        this.application = builder.application;
        this.applicationDescriptor = builder.applicationDescriptor;
        this.local = builder.local;
        this.config = builder.siteDescriptor != null && builder.siteDescriptor.getForm() != null ? new FormJson(
            builder.siteDescriptor.getForm(), builder.localeMessageResolver )
            : null;
        this.authConfig =
            builder.authDescriptor != null && builder.authDescriptor.getConfig() != null
                ? new FormJson( builder.authDescriptor.getConfig(), builder.localeMessageResolver )
                : null;
        ImmutableList.Builder<String> mixinNamesBuilder = new ImmutableList.Builder<>();
        if ( builder.siteDescriptor != null && builder.siteDescriptor.getMetaSteps() != null )
        {
            for ( XDataName xDataName : builder.siteDescriptor.getMetaSteps() )
            {
                mixinNamesBuilder.add( xDataName.toString() );
            }
        }
        this.metaStepMixinNames = mixinNamesBuilder.build();
        this.iconUrl = builder.iconUrlResolver.resolve( application.getKey(), applicationDescriptor );
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

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private Application application;

        private ApplicationDescriptor applicationDescriptor;

        private SiteDescriptor siteDescriptor;

        private AuthDescriptor authDescriptor;

        private ApplicationIconUrlResolver iconUrlResolver;

        private LocaleMessageResolver localeMessageResolver;

        private boolean local;

        public ApplicationJson build()
        {
            return new ApplicationJson( this );
        }

        public Builder setApplication( final Application application )
        {
            this.application = application;
            return this;
        }

        public Builder setApplicationDescriptor( final ApplicationDescriptor applicationDescriptor )
        {
            this.applicationDescriptor = applicationDescriptor;
            return this;
        }

        public Builder setSiteDescriptor( final SiteDescriptor siteDescriptor )
        {
            this.siteDescriptor = siteDescriptor;
            return this;
        }

        public Builder setAuthDescriptor( final AuthDescriptor authDescriptor )
        {
            this.authDescriptor = authDescriptor;
            return this;
        }

        public Builder setIconUrlResolver( final ApplicationIconUrlResolver iconUrlResolver )
        {
            this.iconUrlResolver = iconUrlResolver;
            return this;
        }

        public Builder setLocaleMessageResolver( final LocaleMessageResolver localeMessageResolver )
        {
            this.localeMessageResolver = localeMessageResolver;
            return this;
        }

        public Builder setLocal( final boolean local )
        {
            this.local = local;
            return this;
        }
    }

}
