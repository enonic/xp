package com.enonic.xp.project;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.site.SiteConfig;
import com.enonic.xp.site.SiteConfigs;

import static java.util.Objects.requireNonNull;


@NullMarked
public final class CreateProjectParams
{
    private final ProjectName name;

    private final @Nullable String displayName;

    private final @Nullable String description;

    private final @Nullable Locale language;

    private final List<ProjectName> parents;

    private final boolean isPublic;

    private final SiteConfigs siteConfigs;

    private final boolean forceInitialization;

    private CreateProjectParams( final Builder builder )
    {
        this.name = requireNonNull( builder.name, "name is required" );
        this.displayName = builder.displayName;
        this.description = builder.description;
        this.language = builder.language;
        this.parents = builder.parents.build();
        this.siteConfigs = builder.siteConfigs.build();
        this.forceInitialization = builder.forceInitialization;
        this.isPublic = builder.isPublic;
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

    public boolean isPublic()
    {
        return isPublic;
    }

    public boolean isForceInitialization()
    {
        return forceInitialization;
    }

    public static final class Builder
    {

        private @Nullable ProjectName name;

        private @Nullable String displayName;

        private @Nullable String description;

        private @Nullable Locale language;

        private final ImmutableList.Builder<ProjectName> parents = ImmutableList.builder();

        private boolean isPublic;

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

        public Builder addParents( final Collection<ProjectName> parent )
        {
            this.parents.addAll( parent );
            return this;
        }

        public Builder parent( final @Nullable ProjectName parent )
        {
            if ( parent != null )
            {
                this.parents.add( parent );
            }
            return this;
        }

        public Builder isPublic( final boolean isPublic )
        {
            this.isPublic = isPublic;
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

        public CreateProjectParams build()
        {
            return new CreateProjectParams( this );
        }
    }
}
