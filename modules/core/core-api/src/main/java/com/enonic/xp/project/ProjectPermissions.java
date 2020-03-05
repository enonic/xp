package com.enonic.xp.project;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang.StringUtils;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;

@PublicApi
public final class ProjectPermissions
{
    private Map<ProjectPermissionsLevel, PrincipalKeys> permissions;

    private ProjectPermissions( Builder builder )
    {
        this.permissions =
            Map.of( ProjectPermissionsLevel.OWNER, builder.owner.build(), ProjectPermissionsLevel.EXPERT, builder.expert.build(),
                    ProjectPermissionsLevel.CONTRIBUTOR, builder.contributor.build() );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final ProjectPermissions source )
    {
        return new Builder( source );
    }

    public PrincipalKeys getOwner()
    {
        return permissions.get( ProjectPermissionsLevel.OWNER );
    }

    public PrincipalKeys getExpert()
    {
        return permissions.get( ProjectPermissionsLevel.EXPERT );
    }

    public PrincipalKeys getContributor()
    {
        return permissions.get( ProjectPermissionsLevel.CONTRIBUTOR );
    }

    public PrincipalKeys getPermissions( final Collection<ProjectPermissionsLevel> permissions )
    {
        final PrincipalKeys.Builder result = PrincipalKeys.create();
        permissions.forEach( permission -> result.addAll( this.permissions.get( permission ) ) );

        return result.build();
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }
        final ProjectPermissions that = (ProjectPermissions) o;
        return Objects.equals( permissions, that.permissions );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( permissions );
    }

    public static final class Builder
    {
        private PrincipalKeys.Builder owner = PrincipalKeys.create();

        private PrincipalKeys.Builder expert = PrincipalKeys.create();

        private PrincipalKeys.Builder contributor = PrincipalKeys.create();

        private Builder()
        {
        }

        private Builder( final ProjectPermissions source )
        {
            if ( source != null )
            {
                owner.addAll( source.getOwner() );
                expert.addAll( source.getExpert() );
                contributor.addAll( source.getContributor() );
            }
        }

        public Builder addOwner( final String owner )
        {
            if ( StringUtils.isNotBlank( owner ) )
            {
                this.owner.add( PrincipalKey.from( owner ) );
            }
            return this;
        }

        public Builder addOwner( final PrincipalKey owner )
        {
            if ( owner != null )
            {
                this.owner.add( owner );
            }
            return this;
        }

        public Builder addExpert( final String expert )
        {
            if ( StringUtils.isNotBlank( expert ) )
            {
                this.expert.add( PrincipalKey.from( expert ) );
            }
            return this;
        }

        public Builder addExpert( final PrincipalKey expert )
        {
            if ( expert != null )
            {
                this.expert.add( expert );
            }
            return this;
        }

        public Builder addContributor( final String contributor )
        {
            if ( StringUtils.isNotBlank( contributor ) )
            {
                this.contributor.add( PrincipalKey.from( contributor ) );
            }
            return this;
        }

        public Builder addContributor( final PrincipalKey contributor )
        {
            if ( contributor != null )
            {
                this.contributor.add( contributor );
            }
            return this;
        }

        public ProjectPermissions build()
        {
            return new ProjectPermissions( this );
        }
    }
}
