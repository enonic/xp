package com.enonic.xp.core.impl.project;

import java.util.stream.Collectors;

import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.node.Node;
import com.enonic.xp.project.ProjectReadAccess;
import com.enonic.xp.project.ProjectReadAccessType;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.PrincipalRelationship;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.acl.Permission;

public final class GetProjectReadAccessCommand
    extends AbstractProjectReadAccessCommand
{
    private GetProjectReadAccessCommand( final Builder builder )
    {
        super( builder );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ProjectReadAccess execute()
    {
        return doGetProjectReadAccess();
    }

    private ProjectReadAccess doGetProjectReadAccess()
    {
        final ProjectReadAccess.Builder readAccess = ProjectReadAccess.create();

        final Node contentRootNode = projectRepoContext.callWith( () -> this.nodeService.getByPath( ContentConstants.CONTENT_ROOT_PATH ) );

        final boolean hasEveryoneReadPermission = contentRootNode.getPermissions().isAllowedFor( RoleKeys.EVERYONE, Permission.READ );
        final PrincipalKeys viewerRoleMembers =
            PrincipalKeys.from( securityService.getRelationships( ProjectRoles.VIEWER.getRoleKey( this.projectName ) ).
                stream().
                map( PrincipalRelationship::getTo ).
                collect( Collectors.toList() ) );

        if ( hasEveryoneReadPermission )
        {
            readAccess.setType( ProjectReadAccessType.PUBLIC );
        }
        else
        {
            if ( viewerRoleMembers.isEmpty() )
            {
                readAccess.setType( ProjectReadAccessType.PRIVATE );
            }
            else
            {
                readAccess.setType( ProjectReadAccessType.CUSTOM );
                readAccess.addPrincipals( viewerRoleMembers.getSet() );
            }
        }
        return readAccess.build();
    }

    public static final class Builder
        extends AbstractProjectReadAccessCommand.Builder<Builder>
    {
        private Builder()
        {
        }

        public GetProjectReadAccessCommand build()
        {
            validate();
            return new GetProjectReadAccessCommand( this );
        }
    }
}
