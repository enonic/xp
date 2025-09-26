package com.enonic.xp.lib.node;

import com.enonic.xp.lib.node.mapper.NodeMapper;
import com.enonic.xp.node.MoveNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.node.NodePath;

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

    @Override
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
            final NodePath newPath = target.getAsNodePath();
            return nodeService.move( MoveNodeParams.create().nodeId( node.id() ).newParentPath( newPath ).build() )
                .getMovedNodes()
                .getFirst()
                .getNode();
        }
        else if ( target.isNewName() )
        {
            final NodeName newName = target.getAsNodeName();
            return nodeService.move( MoveNodeParams.create().nodeId( node.id() ).newName( newName ).build() )
                .getMovedNodes()
                .getFirst()
                .getNode();
        }
        else
        {
            final NodePath targetPath = target.getAsNodePath();
            final NodePath targetParent = targetPath.getParentPath();
            final boolean movedToNewParent = !targetParent.equals( node.parentPath() );

            return nodeService.move( MoveNodeParams.create()
                                         .nodeId( node.id() )
                                         .newParentPath( movedToNewParent ? targetParent : null )
                                         .newName( targetPath.getName() )
                                         .build() ).getMovedNodes().getFirst().getNode();
        }
    }

    private NodeMapper convert( final Node node )
    {
        return node == null ? null : new NodeMapper( node );
    }

    private static class Target
    {
        private final String value;

        Target( final String value )
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
            return new NodePath( this.value.endsWith( "/" ) ? this.value.substring( 0, this.value.length() - 1 ) : this.value );
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
