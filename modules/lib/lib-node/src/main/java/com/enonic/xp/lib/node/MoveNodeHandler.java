package com.enonic.xp.lib.node;

import java.util.UUID;

import com.enonic.xp.lib.node.mapper.NodeMapper;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.RenameNodeParams;

public final class MoveNodeHandler
    extends AbstractNodeHandler
{
    private final NodeKey source;

    private final Target target;

    private MoveNodeHandler( final Builder builder )
    {
        super( builder );
        source = builder.source;
        target = builder.target;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public Object execute()
    {
        return convert( executeMove() );
    }

    private Node executeMove()
    {
        final Node node = doGetNode( this.source );

        if ( node == null )
        {
            throw new NodeNotFoundException( "Node to move with key [" + this.source + "] not found" );
        }

        if ( target.isNewParentFolder() )
        {
            return move( node.id(), target.getAsNodePath() );
        }
        else if ( target.isNewName() )
        {
            return rename( node.id(), target.getAsNodeName() );
        }
        else
        {
            return moveAndRename( node );
        }
    }

    private Node moveAndRename( final Node node )
    {
        final NodePath targetPath = target.getAsNodePath();
        final NodePath targetParent = targetPath.getParentPath();
        final boolean movedToNewParent = !targetParent.equals( node.parentPath() );

        if ( movedToNewParent )
        {
            // First rename the node to a temporary unique name to avoid clashing with siblings with target name in source parent or with siblings with source name in target parent
            rename( node.id(), uniqueName() );
            move( node.id(), targetParent );
        }

        return rename( node.id(), NodeName.from( targetPath.getName() ) );
    }

    private NodeMapper convert( final Node node )
    {
        return node == null ? null : new NodeMapper( node );
    }

    private Node move( final NodeId sourceId, final NodePath newPath )
    {
        return nodeService.move( sourceId, newPath, null );
    }

    private Node rename( final NodeId sourceId, final NodeName newName )
    {
        final RenameNodeParams renameParams = RenameNodeParams.create().
            nodeId( sourceId ).
            nodeName( newName ).
            build();
        return nodeService.rename( renameParams );
    }

    private NodeName uniqueName()
    {
        return NodeName.from( UUID.randomUUID().toString() );
    }

    private static class Target
    {
        private final String value;

        public Target( final String value )
        {
            this.value = value;
        }

        boolean isNewParentFolder()
        {
            return this.value.endsWith( "/" );
        }

        boolean isNewName()
        {
            return !value.startsWith( "/" );
        }

        NodeName getAsNodeName()
        {
            return NodeName.from( this.value );
        }

        NodePath getAsNodePath()
        {
            return NodePath.create( this.value ).trailingDivider( false ).build();
        }
    }

    public static final class Builder
        extends AbstractNodeHandler.Builder<Builder>
    {
        private NodeKey source;

        private Target target;

        private Builder()
        {
        }

        public Builder source( final NodeKey val )
        {
            source = val;
            return this;
        }

        public Builder target( final String val )
        {
            target = new Target( val );
            return this;
        }

        public MoveNodeHandler build()
        {
            return new MoveNodeHandler( this );
        }
    }
}
