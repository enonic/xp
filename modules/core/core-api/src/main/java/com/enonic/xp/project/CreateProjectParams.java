package com.enonic.xp.project;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.site.SiteConfig;
import com.enonic.xp.site.SiteConfigs;

@PublicApi
public final class CreateProjectParams
{
    private final ProjectName name;

    private final String displayName;

    private final String description;

    private final List<ProjectName> parents;

    private final AccessControlList permissions;

    private final SiteConfigs siteConfigs;

    private final boolean forceInitialization;

    private CreateProjectParams( final Builder builder )
    {
        this.name = builder.name;
        this.displayName = builder.displayName;
        this.description = builder.description;
        this.parents = builder.parents.build();
        this.siteConfigs = builder.siteConfigs.build();
        this.forceInitialization = builder.forceInitialization;
        this.permissions = builder.permissions;
    }

    public static Builder create()
    {
        return new Builder();
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

    public List<ProjectName> getParents()
    {
        return parents;
    }

    public ProjectName getParent()
    {
        return !parents.isEmpty() ? parents.get( 0 ) : null;
    }

    public SiteConfigs getSiteConfigs()
    {
        return siteConfigs;
    }

    public AccessControlList getPermissions()
    {
        return permissions;
    }

    public boolean isForceInitialization()
    {
        return forceInitialization;
    }

    public static final class Builder
    {

        private ProjectName name;

        private String displayName;

        private String description;

        private final ImmutableList.Builder<ProjectName> parents = ImmutableList.builder();

        private AccessControlList permissions;

        private boolean forceInitialization = false;

        private final SiteConfigs.Builder siteConfigs = SiteConfigs.create();

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

        public Builder addParents( final Collection<ProjectName> parent )
        {
            this.parents.addAll( parent );
            return this;
        }

        public Builder parent( final ProjectName parent )
        {
            if ( parent != null )
            {
                this.parents.add( parent );
            }
            return this;
        }

        public Builder permissions( final AccessControlList permissions )
        {
            this.permissions = permissions;
            return this;
        }

        public Builder forceInitialization( final boolean forceInitialization )
        {
            this.forceInitialization = forceInitialization;
            return this;
        }

        public Builder addSiteConfig( final SiteConfig siteConfig )
        {
            this.siteConfigs.add( siteConfig );
            return this;
        }

        private void validate()
        {
            Objects.requireNonNull( name, "name is required" );
        }

        public CreateProjectParams build()
        {
            validate();
            return new CreateProjectParams( this );
        }
    }
}
