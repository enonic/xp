package com.enonic.xp.core.impl.content;


import java.util.Set;

import com.google.common.base.Preconditions;

import com.enonic.xp.content.CompareStatus;
import com.enonic.xp.content.UndoPendingDeleteContentParams;
import com.enonic.xp.node.FindNodesByParentParams;
import com.enonic.xp.node.FindNodesByParentResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeComparison;
import com.enonic.xp.node.NodeComparisons;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodePath;
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
        final NodeComparisons compare = this.nodeService.compare( nodeIds, this.params.getTarget() );

        Set<NodeComparison> pendingDeleteNodes = compare.getWithStatus( CompareStatus.PENDING_DELETE );

        for ( final NodeComparison nodeComparison : pendingDeleteNodes )
        {
            ensureValidParent( nodeComparison );
            removePendingDeleteState( nodeComparison );
        }

        for ( NodeId nodeId : nodeIds )
        {
            this.undoDeleteContent( this.getAllChildren( nodeId ) );
        }
    }

    private void removePendingDeleteState( final NodeComparison nodeComparison )
    {
        final SetNodeStateResult result = this.nodeService.setNodeState( SetNodeStateParams.create().
            nodeId( nodeComparison.getNodeId() ).
            nodeState( NodeState.DEFAULT ).
            build() );

        pendingDeleteUndoneContents += result.getUpdatedNodes().getSize();
    }

    private void ensureValidParent( final NodeComparison nodeComparison )
    {
        final NodePath parentPath = nodeComparison.getSourcePath().getParentPath();

        if ( ContentNodeHelper.translateNodePathToContentPath( parentPath ).isRoot() )
        {
            return;
        }

        final Node parentNode = this.nodeService.getByPath( parentPath );

        if ( parentNode == null )
        {
            throw new IllegalArgumentException( "Parent with path [" + parentPath + "] does not exists" );
        }

        final NodeComparison parentState = this.nodeService.compare( parentNode.id(), this.params.getTarget() );

        if ( CompareStatus.PENDING_DELETE == parentState.getCompareStatus() )
        {
            removePendingDeleteState( parentState );
        }

        ensureValidParent( parentState );
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
