package com.enonic.xp.project;

import java.util.List;
import java.util.Locale;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.site.SiteConfig;
import com.enonic.xp.site.SiteConfigs;

import static java.util.Objects.requireNonNull;


@NullMarked
public final class Project
{
    private final ProjectName name;

    private final @Nullable String displayName;

    private final @Nullable String description;

    private final @Nullable Locale language;

    private final List<ProjectName> parents;

    private final @Nullable Attachment icon;

    private final SiteConfigs siteConfigs;

    private Project( Builder builder )
    {
        this.name = requireNonNull( builder.name, "name is required" );
        this.displayName = builder.displayName;
        this.description = builder.description;
        this.language = builder.language;
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

    public @Nullable String getDisplayName()
    {
        return displayName;
    }

    public @Nullable String getDescription()
    {
        return description;
    }

    public @Nullable Locale getLanguage()
    {
        return language;
    }

    public @Nullable Attachment getIcon()
    {
        return icon;
    }

    public List<ProjectName> getParents()
    {
        return parents;
    }

    public @Nullable ProjectName getParent()
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

        private @Nullable ProjectName name;

        private @Nullable String displayName;

        private @Nullable String description;

        private @Nullable Locale language;

        private final ImmutableList.Builder<ProjectName> parents = ImmutableList.builder();

        private @Nullable Attachment icon;

        private Builder()
        {
        }

        public Builder name( final ProjectName value )
        {
            this.name = value;
            return this;
        }

        public Builder displayName( final @Nullable String displayName )
        {
            this.displayName = displayName;
            return this;
        }

        public Builder description( final @Nullable String description )
        {
            this.description = description;
            return this;
        }

        public Builder language( final @Nullable Locale language )
        {
            this.language = language;
            return this;
        }

        public Builder icon( final @Nullable Attachment icon )
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

        public Project build()
        {
            return new Project( this );
        }
    }
}
