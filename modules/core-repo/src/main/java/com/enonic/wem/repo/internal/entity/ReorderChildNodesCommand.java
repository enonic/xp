package com.enonic.wem.repo.internal.entity;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.ReorderChildNodeParams;
import com.enonic.wem.api.node.ReorderChildNodesParams;
import com.enonic.wem.api.node.ReorderChildNodesResult;

public class ReorderChildNodesCommand
    extends AbstractNodeCommand
{
    private final ReorderChildNodesParams params;

    private ReorderChildNodesCommand( final Builder builder )
    {
        super( builder );
        this.params = builder.params;
    }

    public ReorderChildNodesResult execute()
    {
        final ReorderChildNodesResult.Builder result = ReorderChildNodesResult.create();

        for ( final ReorderChildNodeParams reorderChildNodeParams : params )
        {
            final Node nodeToMove = doGetById( reorderChildNodeParams.getNodeId(), false );
            final Node nodeToMoveBefore =
                reorderChildNodeParams.getMoveBefore() == null ? null : doGetById( reorderChildNodeParams.getMoveBefore(), false );
            final Node parentNode = doGetByPath( nodeToMove.parentPath(), false );

            final Node reorderedNode = ReorderChildNodeCommand.create().
                queryService( this.queryService ).
                nodeDao( this.nodeDao ).
                workspaceService( this.workspaceService ).
                versionService( this.versionService ).
                indexService( this.indexService ).
                parentNode( parentNode ).
                nodeToMove( nodeToMove ).
                nodeToMoveBefore( nodeToMoveBefore ).
                build().
                execute();

            result.addNodeId( reorderedNode.id() );
        }

        return result.build();
    }

    public static Builder create()
    {
        return new Builder();
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
