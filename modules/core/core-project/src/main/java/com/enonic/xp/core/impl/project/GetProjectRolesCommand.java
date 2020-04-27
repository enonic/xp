package com.enonic.xp.core.impl.project;

import java.util.List;
import java.util.stream.Collectors;

import com.enonic.xp.project.ProjectPermissions;
import com.enonic.xp.project.ProjectRole;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalRelationship;

public final class GetProjectRolesCommand
    extends AbstractProjectRolesCommand
{

    private GetProjectRolesCommand( final Builder builder )
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

        doGetRoleMembers( ProjectRole.OWNER ).forEach( projectPermissions::addOwner );
        doGetRoleMembers( ProjectRole.EDITOR ).forEach( projectPermissions::addEditor );
        doGetRoleMembers( ProjectRole.AUTHOR ).forEach( projectPermissions::addAuthor );
        doGetRoleMembers( ProjectRole.CONTRIBUTOR ).forEach( projectPermissions::addContributor );
        doGetRoleMembers( ProjectRole.VIEWER ).forEach( projectPermissions::addViewer );

        return projectPermissions.build();
    }

    private List<PrincipalKey> doGetRoleMembers( final ProjectRole projectRole )
    {
        return securityService.getRelationships( ProjectAccessHelper.createRoleKey( projectName, projectRole ) ).
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

        public GetProjectRolesCommand build()
        {
            validate();
            return new GetProjectRolesCommand( this );
        }

    }
}
