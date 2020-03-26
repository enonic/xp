package com.enonic.xp.core.impl.project;

import com.google.common.base.Preconditions;

import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.node.ApplyNodePermissionsParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.project.ProjectReadAccess;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.PrincipalRelationships;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;

public final class UpdateProjectReadAccessCommand
    extends AbstractProjectReadAccessCommand
{
    private final ProjectReadAccess readAccess;

    private UpdateProjectReadAccessCommand( final Builder builder )
    {
        super( builder );
        this.readAccess = builder.readAccess;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ProjectReadAccess execute()
    {
        return doExecute();
    }

    private ProjectReadAccess doExecute()
    {
        final Node contentRootNode = projectRepoContext.callWith( () -> this.nodeService.getByPath( ContentConstants.CONTENT_ROOT_PATH ) );

        switch ( readAccess.getType() )
        {
            case PUBLIC:
                doAddEveryonePermissions( contentRootNode.id(), contentRootNode.getPermissions() );
                doRemoveAllViewerRoleMembers();
                break;
            case PRIVATE:
                doRemoveEveryonePermissions( contentRootNode.id(), contentRootNode.getPermissions() );
                doRemoveAllViewerRoleMembers();
            case CUSTOM:
                doRemoveEveryonePermissions( contentRootNode.id(), contentRootNode.getPermissions() );
                doSetViewerRoleMembers();
        }

        return readAccess;
    }

    private void doRemoveEveryonePermissions( final NodeId nodeId, final AccessControlList permissions )
    {
        if ( permissions.getEntry( RoleKeys.EVERYONE ) != null )
        {
            this.nodeService.applyPermissions( ApplyNodePermissionsParams.create().
                nodeId( nodeId ).
                permissions( AccessControlList.create( permissions ).
                    remove( RoleKeys.EVERYONE ).
                    build() ).
                build() );
        }
    }

    private void doAddEveryonePermissions( final NodeId nodeId, final AccessControlList permissions )
    {
        if ( permissions.getEntry( RoleKeys.EVERYONE ) == null )
        {
            projectRepoContext.runWith( () -> {
                this.nodeService.applyPermissions( ApplyNodePermissionsParams.create().
                    nodeId( nodeId ).
                    permissions( AccessControlList.create( permissions ).
                        add( AccessControlEntry.create().
                            principal( RoleKeys.EVERYONE ).
                            allow( Permission.READ ).
                            build() ).
                        build() ).
                    build() );
            } );
        }
    }

    private void doRemoveAllViewerRoleMembers()
    {
        this.securityService.removeRelationships( ProjectRoles.VIEWER.getRoleKey( projectName ) );
    }

    private void doSetViewerRoleMembers()
    {
        final PrincipalKey roleKey = ProjectRoles.VIEWER.getRoleKey( this.projectName );
        final PrincipalRelationships oldRoleMembers = securityService.getRelationships( roleKey );
        final PrincipalKeys newRoleMembers = PrincipalKeys.from( readAccess.getPrincipals() );

        doGetAddedMembers( oldRoleMembers, newRoleMembers, roleKey ).
            forEach( securityService::addRelationship );

        doGetRemovedMembers( oldRoleMembers, newRoleMembers ).
            forEach( securityService::removeRelationship );
    }


    public static final class Builder
        extends AbstractProjectReadAccessCommand.Builder<Builder>
    {
        private ProjectReadAccess readAccess;

        private Builder()
        {
        }

        public Builder readAccess( final ProjectReadAccess readAccess )
        {
            this.readAccess = readAccess;
            return this;
        }

        void validate()
        {
            super.validate();
            Preconditions.checkNotNull( this.readAccess, "readAccess is required" );
        }

        public UpdateProjectReadAccessCommand build()
        {
            validate();
            return new UpdateProjectReadAccessCommand( this );
        }

    }
}
