package com.enonic.xp.project;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang.StringUtils;

import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;

public final class ProjectPermissions
{
    private Map<ProjectPermissionsLevel, PrincipalKeys> permissions;

    private ProjectPermissions( Builder builder )
    {
        this.permissions =
            Map.of( ProjectPermissionsLevel.OWNER, builder.owner.build(), ProjectPermissionsLevel.EDITOR, builder.editor.build(),
                    ProjectPermissionsLevel.AUTHOR, builder.author.build(), ProjectPermissionsLevel.CONTRIBUTOR,
                    builder.contributor.build() );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public PrincipalKeys getOwner()
    {
        return permissions.get( ProjectPermissionsLevel.OWNER );
    }

    public PrincipalKeys getEditor()
    {
        return permissions.get( ProjectPermissionsLevel.EDITOR );
    }

    public PrincipalKeys getAuthor()
    {
        return permissions.get( ProjectPermissionsLevel.AUTHOR );
    }

    public PrincipalKeys getContributor()
    {
        return permissions.get( ProjectPermissionsLevel.CONTRIBUTOR );
    }

    public PrincipalKeys getPermission( final ProjectPermissionsLevel projectPermissionsLevel )
    {
        return doGetPermission( projectPermissionsLevel );
    }

    public PrincipalKeys getPermissions( final Collection<ProjectPermissionsLevel> projectPermissionsLevels )
    {
        {
            final PrincipalKeys.Builder result = PrincipalKeys.create();
            projectPermissionsLevels.forEach( permission -> result.addAll( doGetPermission( permission ) ) );

            return result.build();
        }
    }


    private PrincipalKeys doGetPermission( final ProjectPermissionsLevel projectPermissionsLevel )
    {
        return this.permissions.get( projectPermissionsLevel );
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

        private PrincipalKeys.Builder editor = PrincipalKeys.create();

        private PrincipalKeys.Builder author = PrincipalKeys.create();

        private PrincipalKeys.Builder contributor = PrincipalKeys.create();

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

        public Builder addEditor( final String editor )
        {
            if ( StringUtils.isNotBlank( editor ) )
            {
                this.editor.add( PrincipalKey.from( editor ) );
            }
            return this;
        }

        public Builder addEditor( final PrincipalKey editor )
        {
            if ( editor != null )
            {
                this.editor.add( editor );
            }
            return this;
        }

        public Builder addAuthor( final String author )
        {
            if ( StringUtils.isNotBlank( author ) )
            {
                this.author.add( PrincipalKey.from( author ) );
            }
            return this;
        }

        public Builder addAuthor( final PrincipalKey author )
        {
            if ( author != null )
            {
                this.author.add( author );
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
