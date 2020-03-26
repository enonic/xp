package com.enonic.xp.core.impl.project;

import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;

import com.enonic.xp.project.ProjectPermissions;
import com.enonic.xp.project.ProjectPermissionsLevel;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.PrincipalRelationship;
import com.enonic.xp.security.PrincipalRelationships;

public final class UpdateProjectPermissionsCommand
    extends AbstractProjectRolesCommand
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

        doSetRoleMembers( ProjectPermissionsLevel.OWNER, ProjectRoles.OWNER ).
            forEach( projectPermissions::addOwner );
        doSetRoleMembers( ProjectPermissionsLevel.EDITOR, ProjectRoles.EDITOR ).
            forEach( projectPermissions::addEditor );
        doSetRoleMembers( ProjectPermissionsLevel.AUTHOR, ProjectRoles.AUTHOR ).
            forEach( projectPermissions::addAuthor );
        doSetRoleMembers( ProjectPermissionsLevel.CONTRIBUTOR, ProjectRoles.CONTRIBUTOR ).
            forEach( projectPermissions::addContributor );

        return projectPermissions.build();
    }

    private Set<PrincipalKey> doSetRoleMembers( final ProjectPermissionsLevel projectPermissionsLevel, final ProjectRoles projectRole )
    {
        final PrincipalKey roleKey = projectRole.getRoleKey( this.projectName );
        final PrincipalRelationships roleMembers = securityService.getRelationships( roleKey );
        final PrincipalKeys permissionLevelMembers = this.permissions.getPermission( projectPermissionsLevel );

        doGetAddedMembers( roleMembers, permissionLevelMembers, roleKey ).
            forEach( securityService::addRelationship );

        doGetRemovedMembers( roleMembers, permissionLevelMembers ).
            forEach( securityService::removeRelationship );

        return securityService.getRelationships( roleKey ).stream().
            map( PrincipalRelationship::getTo ).
            collect( Collectors.toSet() );
    }

    public static final class Builder
        extends AbstractProjectRolesCommand.Builder<Builder>
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
