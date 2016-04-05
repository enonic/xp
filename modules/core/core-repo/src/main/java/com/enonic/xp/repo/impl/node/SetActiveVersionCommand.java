package com.enonic.xp.repo.impl.node;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.security.acl.Permission;

public class SetActiveVersionCommand
    extends AbstractNodeCommand
{
    private final NodeId nodeId;

    private final NodeVersionId nodeVersionId;

    private final static Permission REQUIRED_PERMISSION = Permission.MODIFY;

    private SetActiveVersionCommand( final Builder builder )
    {
        super( builder );
        nodeId = builder.nodeId;
        nodeVersionId = builder.nodeVersionId;
    }

    public NodeVersionId execute()
    {
        final InternalContext context = InternalContext.from( ContextAccessor.current() );

        final Node node = this.storageService.get( nodeVersionId, context );

        if ( node == null )
        {
            throw new NodeNotFoundException(
                "Cannot find nodeVersion [" + this.nodeVersionId + "] in branch " + context.getBranch().getName() );
        }

        if ( !node.id().equals( nodeId ) )
        {
            throw new NodeNotFoundException( "NodeVersionId [" + nodeVersionId + "] not a version of Node with id [" + nodeId + "]" );
        }

        NodePermissionsResolver.requireContextUserPermissionOrAdmin( REQUIRED_PERMISSION, node );

        this.storageService.updateVersion( node, nodeVersionId, context );

        return nodeVersionId;
    }

    public static Builder create()
    {
        return new Builder();
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
