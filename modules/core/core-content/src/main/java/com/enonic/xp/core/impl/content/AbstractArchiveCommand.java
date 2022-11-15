package com.enonic.xp.core.impl.content;

import com.enonic.xp.node.FindNodesByParentParams;
import com.enonic.xp.node.FindNodesByParentResult;
import com.enonic.xp.node.NodeCommitEntry;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.RefreshMode;

abstract class AbstractArchiveCommand
    extends AbstractContentCommand
{
    AbstractArchiveCommand( final Builder builder )
    {
        super( builder );
    }

    protected void commitNode( final NodeId nodeId, final String message )
    {
        final NodeCommitEntry commitEntry = NodeCommitEntry.create().message( message ).build();

        nodeService.refresh( RefreshMode.ALL );

        final FindNodesByParentResult movedTree =
            nodeService.findByParent( FindNodesByParentParams.create().recursive( true ).parentId( nodeId ).build() );

        nodeService.commit( commitEntry, NodeIds.create().addAll( movedTree.getNodeIds() ).add( nodeId ).build() );
    }

    public abstract static class Builder<B extends Builder<B>>
        extends AbstractContentCommand.Builder<B>
    {
    }
}
