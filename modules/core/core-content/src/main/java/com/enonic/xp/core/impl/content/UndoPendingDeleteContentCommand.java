package com.enonic.xp.core.impl.content;


import java.util.Set;

import com.google.common.base.Preconditions;

import com.enonic.xp.content.CompareStatus;
import com.enonic.xp.content.UndoPendingDeleteContentParams;
import com.enonic.xp.node.FindNodesByParentParams;
import com.enonic.xp.node.FindNodesByParentResult;
import com.enonic.xp.node.NodeComparison;
import com.enonic.xp.node.NodeComparisons;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeState;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.node.SetNodeStateParams;
import com.enonic.xp.node.SetNodeStateResult;


final class UndoPendingDeleteContentCommand
    extends AbstractContentCommand
{
    private final UndoPendingDeleteContentParams params;

    private int pendingDeleteUndoneContents;

    private UndoPendingDeleteContentCommand( final Builder builder )
    {
        super( builder );
        this.params = builder.params;
    }

    public static Builder create()
    {
        return new Builder();
    }

    int execute()
    {
        this.nodeService.refresh( RefreshMode.ALL );
        doExecute();
        nodeService.refresh( RefreshMode.ALL );
        return pendingDeleteUndoneContents;
    }

    private void doExecute()
    {
        this.undoDeleteContent( NodeIds.from( this.params.getContentIds().asStrings() ) );
    }

    private void undoDeleteContent( final NodeIds nodeIds )
    {
        final NodeComparisons compare = this.nodeService.compare( nodeIds, this.params.getBranch() );

        Set<NodeComparison> pendingDeleteNodes = compare.getWithStatus( CompareStatus.PENDING_DELETE );
        for ( final NodeComparison nodeComparison : pendingDeleteNodes )
        {
            final SetNodeStateResult setNodeStateResult = this.nodeService.setNodeState( SetNodeStateParams.create().
                nodeId( nodeComparison.getNodeId() ).
                nodeState( NodeState.DEFAULT ).
                build() );

            pendingDeleteUndoneContents += setNodeStateResult.getUpdatedNodes().getSize();
        }

        for ( NodeId nodeId : nodeIds )
        {
            this.undoDeleteContent( this.getAllChildren( nodeId ) );
        }
    }

    private NodeIds getAllChildren( final NodeId nodeToDelete )
    {
        final FindNodesByParentResult findNodesByParentResult = this.nodeService.findByParent( FindNodesByParentParams.create().
            parentId( nodeToDelete ).
            recursive( true ).
            build() );

        return findNodesByParentResult.getNodeIds();
    }

    public static class Builder
        extends AbstractContentCommand.Builder<Builder>
    {
        private UndoPendingDeleteContentParams params;

        public Builder params( final UndoPendingDeleteContentParams params )
        {
            this.params = params;
            return this;
        }

        @Override
        void validate()
        {
            Preconditions.checkNotNull( params );
        }

        public UndoPendingDeleteContentCommand build()
        {
            validate();
            return new UndoPendingDeleteContentCommand( this );
        }
    }

}
