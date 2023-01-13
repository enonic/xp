package com.enonic.xp.repo.impl.node;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeAccessException;
import com.enonic.xp.node.NodeBranchEntry;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.security.acl.Permission;
import com.enonic.xp.security.auth.AuthenticationInfo;

public class SetActiveVersionCommand
    extends AbstractNodeCommand
{
    private final NodeId nodeId;

    private final NodeVersionId nodeVersionId;

    private SetActiveVersionCommand( final Builder builder )
    {
        super( builder );
        nodeId = builder.nodeId;
        nodeVersionId = builder.nodeVersionId;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public NodeVersionId execute()
    {
        final InternalContext context = InternalContext.from( ContextAccessor.current() );

        final Node node = this.nodeStorageService.get( nodeVersionId, context );

        if ( node == null )
        {
            throw new NodeNotFoundException(
                "Cannot find nodeVersion [" + this.nodeVersionId + "] in branch " + context.getBranch().getValue() );
        }

        if ( !node.id().equals( nodeId ) )
        {
            throw new NodeNotFoundException( "NodeVersionId [" + nodeVersionId + "] not a version of Node with id [" + nodeId + "]" );
        }

        final NodeBranchEntry nodeBranchEntry = this.nodeStorageService.getBranchNodeVersion( nodeId, context );

        final AuthenticationInfo authInfo = ContextAccessor.current().getAuthInfo();

        final boolean hasPermission = NodePermissionsResolver.userHasPermission( authInfo, Permission.MODIFY, node.getPermissions() );

        if ( !hasPermission )
        {
            throw new NodeAccessException( authInfo.getUser(), nodeBranchEntry.getNodePath(), Permission.MODIFY );
        }

        final Node updatedNode = Node.create( node )
            .parentPath( nodeBranchEntry.getNodePath().getParentPath() )
            .name( nodeBranchEntry.getNodePath().getName() )
            .build();

        this.nodeStorageService.updateVersion( updatedNode, context );

        return nodeVersionId;
    }

    public static final class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private NodeId nodeId;

        private NodeVersionId nodeVersionId;

        private Builder()
        {
        }

        public Builder nodeId( final NodeId val )
        {
            nodeId = val;
            return this;
        }

        public Builder nodeVersionId( final NodeVersionId val )
        {
            nodeVersionId = val;
            return this;
        }

        public SetActiveVersionCommand build()
        {
            return new SetActiveVersionCommand( this );
        }
    }
}
