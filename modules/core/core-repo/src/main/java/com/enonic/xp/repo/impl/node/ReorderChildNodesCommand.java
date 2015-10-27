package com.enonic.xp.repo.impl.node;

import com.google.common.base.Preconditions;

import com.enonic.xp.node.Node;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.node.ReorderChildNodeParams;
import com.enonic.xp.node.ReorderChildNodesParams;
import com.enonic.xp.node.ReorderChildNodesResult;

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

        for ( final ReorderChildNodeParams reorderChildNodeParams : params )
        {
            RefreshCommand.create().
                refreshMode( RefreshMode.SEARCH ).
                indexServiceInternal( this.indexServiceInternal ).
                build().
                execute();

            final Node nodeToMove = doGetById( reorderChildNodeParams.getNodeId() );
            final Node nodeToMoveBefore =
                reorderChildNodeParams.getMoveBefore() == null ? null : doGetById( reorderChildNodeParams.getMoveBefore() );

            final Node parentNode = GetNodeByPathCommand.create( this ).
                nodePath( nodeToMove.parentPath() ).
                build().
                execute();

            final Node reorderedNode = ReorderChildNodeCommand.create().
                indexServiceInternal( this.indexServiceInternal ).
                searchService( this.searchService ).
                storageService( this.storageService ).
                parentNode( parentNode ).
                nodeToMove( nodeToMove ).
                nodeToMoveBefore( nodeToMoveBefore ).
                build().
                execute();

            result.addNodeId( reorderedNode.id() );
        }

        return result.build();
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
