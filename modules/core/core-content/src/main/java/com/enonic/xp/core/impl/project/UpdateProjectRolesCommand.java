package com.enonic.xp.core.impl.project;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;

import com.enonic.xp.project.ProjectConstants;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.project.ProjectPermissions;
import com.enonic.xp.project.ProjectPermissionsLevel;
import com.enonic.xp.security.CreateRoleParams;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalRelationship;
import com.enonic.xp.security.PrincipalRelationships;
import com.enonic.xp.security.Role;

import static java.util.stream.Collectors.toList;

public class UpdateProjectRolesCommand
    extends AbstractProjectRolesCommand
{
    private ProjectName projectName;

    private ProjectPermissions oldPermissions;

    private ProjectPermissions newPermissions;

    private UpdateProjectRolesCommand( final Builder builder )
    {
        super( builder );
        this.projectName = builder.projectName;
        this.oldPermissions = builder.oldPermissions;
        this.newPermissions = builder.newPermissions;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public void execute()
    {
        doCreateRoles( projectName );
        doSetRolesMembers( oldPermissions, newPermissions );
    }

    private void doCreateRoles( final ProjectName projectName )
    {
        for ( ProjectRoles projectRole : ProjectRoles.values() )
        {
            final PrincipalKey roleKey = projectRole.getRoleKey( projectName );

            if ( securityService.getRole( roleKey ).isEmpty() )
            {
                securityService.createRole( CreateRoleParams.create().
                    roleKey( PrincipalKey.ofRole( roleKey.getId() ) ).
                    displayName( roleKey.getId() ).
                    build() );
            }
        }
    }

    private void doSetRolesMembers( final ProjectPermissions oldPermissions, final ProjectPermissions newPermissions )
    {
        doSetRoleMembers( oldPermissions, newPermissions, ProjectPermissionsLevel.OWNER, ProjectRoles.OWNER );
        doSetRoleMembers( oldPermissions, newPermissions, ProjectPermissionsLevel.EDITOR, ProjectRoles.EDITOR );
        doSetRoleMembers( oldPermissions, newPermissions, ProjectPermissionsLevel.AUTHOR, ProjectRoles.AUTHOR );
        doSetRoleMembers( oldPermissions, newPermissions, ProjectPermissionsLevel.CONTRIBUTOR, ProjectRoles.CONTRIBUTOR );
    }

    private void doSetRoleMembers( final ProjectPermissions oldPermissions, final ProjectPermissions newPermissions,
                                   final ProjectPermissionsLevel projectPermissionsLevel, final ProjectRoles projectRole )
    {

        final Set<PrincipalKey> addedMembers;
        final Set<PrincipalKey> removedMembers;

        if ( oldPermissions == null )
        {
            addedMembers = newPermissions.getPermission( projectPermissionsLevel ).getSet();
            removedMembers = Set.of();
        }
        else
        {
            addedMembers = doGetAddedMembers( newPermissions, projectPermissionsLevel, projectRole );
            removedMembers = doGetRemovedMembers( oldPermissions, newPermissions, projectPermissionsLevel );
        }

        if ( !addedMembers.isEmpty() )
        {
            doAddRoleMembers( addedMembers, projectName, projectRole );
        }
        if ( !removedMembers.isEmpty() )
        {
            doRemoveRoleMembers( removedMembers, projectName, projectRole );
        }
    }

    private void doAddRoleMembers( final Set<PrincipalKey> principalKeys, final ProjectName projectName, final ProjectRoles projectRole )
    {
        principalKeys.forEach( principalKey -> {
            final PrincipalRelationship rel = PrincipalRelationship.
                from( projectRole.getRoleKey( projectName ) ).
                to( principalKey );

            securityService.addRelationship( rel );
        } );
    }

    private void doRemoveRoleMembers( final Set<PrincipalKey> principalKeys, final ProjectName projectName, final ProjectRoles projectRole )
    {
        principalKeys.
            forEach( principalKey -> {

                final PrincipalRelationship rel = PrincipalRelationship.
                    from( projectRole.getRoleKey( projectName ) ).
                    to( principalKey );

                securityService.removeRelationship( rel );
            } );
    }

    private Set<PrincipalKey> doGetAddedMembers( final ProjectPermissions newPermissions,
                                                 final ProjectPermissionsLevel projectPermissionsLevel, final ProjectRoles projectRole )
    {
        return securityService.getRole( projectRole.getRoleKey( projectName ) ).
            map( Role::getKey ).
            map( roleKey -> {
                final PrincipalRelationships relationships = this.securityService.getRelationships( roleKey );
                final List<PrincipalKey> members = relationships.stream().map( PrincipalRelationship::getTo ).collect( toList() );

                return newPermissions.getPermission( projectPermissionsLevel ).
                    stream().
                    filter( principalKey -> !members.contains( principalKey ) ).
                    collect( Collectors.toSet() );
            } ).
            orElse( Set.of() );
    }

    private Set<PrincipalKey> doGetRemovedMembers( final ProjectPermissions oldProjectPermissions,
                                                   final ProjectPermissions newProjectPermissions,
                                                   final ProjectPermissionsLevel projectPermissionsLevel )
    {
        return oldProjectPermissions.getPermission( projectPermissionsLevel ).
            stream().
            filter( principalKey -> !newProjectPermissions.getPermission( projectPermissionsLevel ).contains( principalKey ) ).
            collect( Collectors.toSet() );
    }

    public static final class Builder
        extends AbstractProjectRolesCommand.Builder<Builder>
    {
        private ProjectName projectName;

        private ProjectPermissions oldPermissions;

        private ProjectPermissions newPermissions;

        private Builder()
        {
        }

        public Builder projectName( final ProjectName projectName )
        {
            this.projectName = projectName;
            return this;
        }

        public Builder oldPermissions( final ProjectPermissions oldProject )
        {
            this.oldPermissions = oldProject;
            return this;
        }

        public Builder newPermissions( final ProjectPermissions newProject )
        {
            this.newPermissions = newProject;
            return this;
        }

        void validate()
        {
            super.validate();
            Preconditions.checkNotNull( this.projectName, "project name is required" );
            Preconditions.checkNotNull( this.newPermissions, "New project permissions is required" );
            Preconditions.checkArgument( !ProjectConstants.DEFAULT_PROJECT_NAME.equals( projectName ), "Default project has no roles" );
        }

        public UpdateProjectRolesCommand build()
        {
            validate();
            return new UpdateProjectRolesCommand( this );
        }

    }
}
