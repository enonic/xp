package com.enonic.xp.core.impl.project;

import java.util.List;
import java.util.stream.Collectors;

import com.enonic.xp.project.ProjectPermissions;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalRelationship;

public final class GetProjectPermissionsCommand
    extends AbstractProjectRolesCommand
{

    private GetProjectPermissionsCommand( final Builder builder )
    {
        super( builder );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ProjectPermissions execute()
    {
        return doGetProjectPermissions();
    }

    private ProjectPermissions doGetProjectPermissions()
    {
        final ProjectPermissions.Builder projectPermissions = ProjectPermissions.create();

        doGetRoleMembers( ProjectRoles.OWNER ).forEach( projectPermissions::addOwner );
        doGetRoleMembers( ProjectRoles.EDITOR ).forEach( projectPermissions::addEditor );
        doGetRoleMembers( ProjectRoles.AUTHOR ).forEach( projectPermissions::addAuthor );
        doGetRoleMembers( ProjectRoles.CONTRIBUTOR ).forEach( projectPermissions::addContributor );

        return projectPermissions.build();
    }

    private List<PrincipalKey> doGetRoleMembers( final ProjectRoles projectRole )
    {
        return securityService.getRelationships( projectRole.getRoleKey( this.projectName ) ).
            stream().
            map( PrincipalRelationship::getTo ).
            collect( Collectors.toList() );
    }

    public static final class Builder
        extends AbstractProjectRolesCommand.Builder<Builder>
    {
        private Builder()
        {
        }

        void validate()
        {
            super.validate();
        }

        public GetProjectPermissionsCommand build()
        {
            validate();
            return new GetProjectPermissionsCommand( this );
        }

    }
}
