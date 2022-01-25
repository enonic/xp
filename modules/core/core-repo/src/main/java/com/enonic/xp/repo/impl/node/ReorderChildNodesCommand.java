package com.enonic.xp.repo.impl.node;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Preconditions;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.node.ReorderChildNodeParams;
import com.enonic.xp.node.ReorderChildNodesParams;
import com.enonic.xp.node.ReorderChildNodesResult;

import static com.enonic.xp.repo.impl.node.NodeConstants.CLOCK;

public class ReorderChildNodesCommand
    extends AbstractNodeCommand
{
    private final ReorderChildNodesParams params;

    private ReorderChildNodesCommand( final Builder builder )
    {
        super( builder );
        this.params = builder.params;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ReorderChildNodesResult execute()
    {
        final ReorderChildNodesResult.Builder result = ReorderChildNodesResult.create();

        final List<Node> parents = new ArrayList<>();

        for ( final ReorderChildNodeParams reorderChildNodeParams : params )
        {
            RefreshCommand.create().refreshMode( RefreshMode.SEARCH ).indexServiceInternal( this.indexServiceInternal ).build().execute();

            final Node nodeToMove = doGetById( reorderChildNodeParams.getNodeId() );
            final Node nodeToMoveBefore =
                reorderChildNodeParams.getMoveBefore() == null ? null : doGetById( reorderChildNodeParams.getMoveBefore() );

            final Node parentNode = parents.stream()
                .filter( node -> node.path().equals( nodeToMove.parentPath() ) )
                .findAny()
                .orElse( GetNodeByPathCommand.create( this ).nodePath( nodeToMove.parentPath() ).build().execute() );

            final Node reorderedNode = ReorderChildNodeCommand.create()
                .indexServiceInternal( this.indexServiceInternal )
                .searchService( this.nodeSearchService )
                .storageService( this.nodeStorageService )
                .parentNode( parentNode )
                .nodeToMove( nodeToMove )
                .nodeToMoveBefore( nodeToMoveBefore )
                .build()
                .execute();

            result.addNodeId( reorderedNode.id() );

            if ( parents.stream().noneMatch( parent -> parent.id().equals( parentNode.id() ) ) )
            {
                parents.add( parentNode );
            }
        }

        parents.forEach( this::processParent );
        parents.forEach( result::addParentNode );

        return result.build();
    }

    private void processParent( final Node parentNode )
    {
        final PropertyTree processedData = params.getProcessor().process( parentNode.data().copy() );
        if ( !processedData.equals( parentNode.data() ) )
        {
            final Node editedNode = Node.create( parentNode ).data( processedData ).timestamp( Instant.now( CLOCK ) ).build();

            StoreNodeCommand.create( this ).node( editedNode ).build().execute();
        }
    }

    public static class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private ReorderChildNodesParams params;

        private Builder()
        {
        }

        public Builder params( final ReorderChildNodesParams params )
        {
            this.params = params;
            return this;
        }

        @Override
        void validate()
        {
            super.validate();
            Preconditions.checkNotNull( this.params );
        }

        public ReorderChildNodesCommand build()
        {
            this.validate();
            return new ReorderChildNodesCommand( this );
        }
    }
}
