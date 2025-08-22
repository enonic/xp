package com.enonic.xp.project;

import java.util.List;
import java.util.Objects;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.site.SiteConfig;
import com.enonic.xp.site.SiteConfigs;

@PublicApi
public final class Project
{
    private final ProjectName name;

    private final String displayName;

    private final String description;

    private final List<ProjectName> parents;

    private final Attachment icon;

    private final SiteConfigs siteConfigs;

    private Project( Builder builder )
    {
        this.name = builder.name;
        this.displayName = builder.displayName;
        this.description = builder.description;
        this.parents = builder.parents.build();
        this.icon = builder.icon;
        this.siteConfigs = builder.siteConfigs.build();
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

    public Attachment getIcon()
    {
        return icon;
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

    public static final class Builder
    {
        private final SiteConfigs.Builder siteConfigs = SiteConfigs.create();

        private ProjectName name;

        private String displayName;

        private String description;

        private final ImmutableList.Builder<ProjectName> parents = ImmutableList.builder();

        private Attachment icon;

        private Builder()
        {
        }

        public Builder name( final ProjectName value )
        {
            this.name = value;
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

        public Builder icon( final Attachment icon )
        {
            this.icon = icon;
            return this;
        }

        public Builder parent( final ProjectName parent )
        {
            this.parents.add( parent );
            return this;
        }

        public Builder addParent( final ProjectName parent )
        {
            this.parents.add( parent );
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


        public Project build()
        {
            validate();
            return new Project( this );
        }
    }
}
