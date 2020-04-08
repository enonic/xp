package com.enonic.xp.core.impl.project;

import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;

import com.enonic.xp.project.ProjectPermissions;
import com.enonic.xp.project.ProjectRole;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.PrincipalRelationship;
import com.enonic.xp.security.PrincipalRelationships;

public final class UpdateProjectPermissionsCommand
    extends AbstractProjectPermissionsCommand
{
    private ProjectPermissions permissions;

    private UpdateProjectPermissionsCommand( final Builder builder )
    {
        super( builder );
        this.permissions = builder.permissions;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ProjectPermissions execute()
    {
        return doSetRolesMembers();
    }

    private ProjectPermissions doSetRolesMembers()
    {
        final ProjectPermissions.Builder projectPermissions = ProjectPermissions.create();

        doSetRoleMembers( ProjectRole.OWNER ).
            forEach( projectPermissions::addOwner );
        doSetRoleMembers( ProjectRole.EDITOR ).
            forEach( projectPermissions::addEditor );
        doSetRoleMembers( ProjectRole.AUTHOR ).
            forEach( projectPermissions::addAuthor );
        doSetRoleMembers( ProjectRole.CONTRIBUTOR ).
            forEach( projectPermissions::addContributor );
        doSetRoleMembers( ProjectRole.VIEWER ).
            forEach( projectPermissions::addViewer );

        return projectPermissions.build();
    }

    private Set<PrincipalKey> doSetRoleMembers( final ProjectRole projectRole )
    {
        final PrincipalKey roleKey = ProjectAccessHelper.createRoleKey( projectName, projectRole );
        final PrincipalRelationships currRoleMembers = securityService.getRelationships( roleKey );
        final PrincipalKeys newRoleMembers = this.permissions.getPermission( projectRole );

        doGetAddedMembers( currRoleMembers, newRoleMembers, roleKey ).
            forEach( securityService::addRelationship );

        doGetRemovedMembers( currRoleMembers, newRoleMembers ).
            forEach( securityService::removeRelationship );

        return securityService.getRelationships( roleKey ).stream().
            map( PrincipalRelationship::getTo ).
            collect( Collectors.toSet() );
    }

    public static final class Builder
        extends AbstractProjectPermissionsCommand.Builder<Builder>
    {
        private ProjectPermissions permissions;

        private Builder()
        {
        }

        public Builder permissions( final ProjectPermissions permissions )
        {
            this.permissions = permissions;
            return this;
        }

        void validate()
        {
            super.validate();
            Preconditions.checkNotNull( this.permissions, "Project permissions is required" );
        }

        public UpdateProjectPermissionsCommand build()
        {
            validate();
            return new UpdateProjectPermissionsCommand( this );
        }

    }
}
