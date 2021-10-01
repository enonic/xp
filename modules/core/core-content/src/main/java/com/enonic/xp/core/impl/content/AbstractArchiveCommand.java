package com.enonic.xp.core.impl.content;

import com.google.common.base.Preconditions;

import com.enonic.xp.node.NodeCommitEntry;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.RefreshMode;

abstract class AbstractArchiveCommand
{
    final NodeService nodeService;

    final ContentNodeTranslator translator;

    AbstractArchiveCommand( final Builder builder )
    {
        this.nodeService = builder.nodeService;
        this.translator = builder.translator;
    }

    protected void commitNode( final NodeId nodeId, final String message )
    {
        final NodeCommitEntry commitEntry = NodeCommitEntry.create().message( message ).build();

        nodeService.refresh( RefreshMode.ALL );
        nodeService.commit( commitEntry, NodeIds.from( nodeId ) );
    }

    public static class Builder<B extends Builder<B>>
    {
        private NodeService nodeService;

        private ContentNodeTranslator translator;

        Builder()
        {
        }

        public B nodeService( final NodeService nodeService )
        {
            this.nodeService = nodeService;
            return (B) this;
        }

        public B translator( final ContentNodeTranslator translator )
        {
            this.translator = translator;
            return (B) this;
        }

        void validate()
        {
            Preconditions.checkNotNull( nodeService, "nodeService cannot be null" );
            Preconditions.checkNotNull( translator, "translator cannot be null" );
        }
    }
}
