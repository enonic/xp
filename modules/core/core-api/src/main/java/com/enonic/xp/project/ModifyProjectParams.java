package com.enonic.xp.project;

import java.time.ZoneId;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.site.SiteConfig;
import com.enonic.xp.site.SiteConfigs;

@PublicApi
public final class ModifyProjectParams
{
    private final ProjectName name;

    private final String displayName;

    private final String description;

    private final SiteConfigs siteConfigs;

    private final ZoneId timeZone;

    private ModifyProjectParams( final Builder builder )
    {
        this.name = builder.name;
        this.displayName = builder.displayName;
        this.description = builder.description;
        this.siteConfigs = builder.siteConfigs.build();
        this.timeZone = builder.timeZone;
    }

    public static Builder create()
    {
        return new Builder();
    }

    @Deprecated
    public static Builder create( final CreateProjectParams params )
    {
        return create().
            name( params.getName() ).
            description( params.getDescription() ).
            displayName( params.getDisplayName() ).
            timeZone( params.getTimeZone() );
    }

    public ProjectName getName()
    {
        return name;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public String getDescription()
    {
        return description;
    }

    public SiteConfigs getSiteConfigs()
    {
        return siteConfigs;
    }

    public ZoneId getTimeZone()
    {
        return timeZone;
    }

    public static final class Builder
    {
        private ProjectName name;

        private String displayName;

        private String description;

        private final SiteConfigs.Builder siteConfigs = SiteConfigs.create();

        private ZoneId timeZone;

        private Builder()
        {
        }

        public Builder name( final ProjectName name )
        {
            this.name = name;
            return this;
        }

        public Builder displayName( final String displayName )
        {
            this.displayName = displayName;
            return this;
        }

        public Builder description( final String description )
        {
            this.description = description;
            return this;
        }

        public Builder addSiteConfig( final SiteConfig siteConfig )
        {
            this.siteConfigs.add( siteConfig );
            return this;
        }

        public Builder timeZone( final ZoneId timeZone )
        {
            this.timeZone = timeZone;
            return this;
        }

        private void validate()
        {
            Preconditions.checkNotNull( name, "projectName cannot be null" );
        }

        public ModifyProjectParams build()
        {
            validate();
            return new ModifyProjectParams( this );
        }
    }
}
