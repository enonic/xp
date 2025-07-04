package com.enonic.xp.project;

import java.util.Collection;
import java.util.Map;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;

import static java.util.Objects.requireNonNullElse;

@PublicApi
public final class ProjectPermissions
{
    private final Map<ProjectRole, PrincipalKeys> permissions;

    private ProjectPermissions( Builder builder )
    {
        this.permissions = Map.of( ProjectRole.OWNER, builder.owner.build(), ProjectRole.EDITOR, builder.editor.build(), ProjectRole.AUTHOR,
                                   builder.author.build(), ProjectRole.CONTRIBUTOR, builder.contributor.build(), ProjectRole.VIEWER,
                                   builder.viewer.build() );
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
        return permissions.get( ProjectRole.OWNER );
    }

    public PrincipalKeys getEditor()
    {
        return permissions.get( ProjectRole.EDITOR );
    }

    public PrincipalKeys getAuthor()
    {
        return permissions.get( ProjectRole.AUTHOR );
    }

    public PrincipalKeys getContributor()
    {
        return permissions.get( ProjectRole.CONTRIBUTOR );
    }

    public PrincipalKeys getViewer()
    {
        return permissions.get( ProjectRole.VIEWER );
    }

    public PrincipalKeys getPermission( final ProjectRole projectRole )
    {
        return permissions.get( projectRole );
    }

    public PrincipalKeys getPermissions( final Collection<ProjectRole> projectRoles )
    {
        return projectRoles.stream()
            .flatMap( role -> requireNonNullElse( permissions.get( role ), PrincipalKeys.empty() ).stream() )
            .collect( PrincipalKeys.collector() );
    }

    public static final class Builder
    {
        private final PrincipalKeys.Builder owner = PrincipalKeys.create();

        private final PrincipalKeys.Builder editor = PrincipalKeys.create();

        private final PrincipalKeys.Builder author = PrincipalKeys.create();

        private final PrincipalKeys.Builder contributor = PrincipalKeys.create();

        private final PrincipalKeys.Builder viewer = PrincipalKeys.create();

        private Builder()
        {

        }

        private Builder( final ProjectPermissions source )
        {
            this.owner.addAll( source.getOwner() );
            this.editor.addAll( source.getEditor() );
            this.author.addAll( source.getAuthor() );
            this.contributor.addAll( source.getContributor() );
            this.viewer.addAll( source.getViewer() );
        }

        public Builder addOwner( final PrincipalKey owner )
        {
            this.owner.add( owner );
            return this;
        }

        public Builder addEditor( final PrincipalKey editor )
        {
            this.editor.add( editor );
            return this;
        }

        public Builder addAuthor( final PrincipalKey author )
        {
            this.author.add( author );
            return this;
        }

        public Builder addContributor( final PrincipalKey contributor )
        {
            this.contributor.add( contributor );
            return this;
        }

        public Builder addViewer( final PrincipalKey viewer )
        {
            this.viewer.add( viewer );
            return this;
        }

        public ProjectPermissions build()
        {
            return new ProjectPermissions( this );
        }
    }
}
