package com.enonic.xp.lib.node;

import com.enonic.xp.lib.node.mapper.NodeCommitEntryMapper;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeCommitEntry;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodePaths;

public final class CommitNodeHandler
    extends AbstractNodeHandler
{
    private final NodeKeys keys;

    private final String message;

    private CommitNodeHandler( final Builder builder )
    {
        super( builder );
        keys = builder.keys;
        message = builder.message;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public NodeCommitEntryMapper execute()
    {
        final NodeCommitEntry nodeCommitEntry = NodeCommitEntry.create().
            message( message ).
            build();
        final NodeIds nodeIds = getNodeIds();
        final NodeCommitEntry createdCommitEntry = nodeService.commit( nodeCommitEntry, nodeIds );
        return new NodeCommitEntryMapper( createdCommitEntry );
    }

    public NodeIds getNodeIds()
    {
        final NodeIds.Builder nodeIds = NodeIds.create();
        keys.stream().
            filter( NodeKey::isId ).
            map( NodeKey::getAsNodeId ).
            forEach( nodeIds::add );

        final NodePaths.Builder nodePaths = NodePaths.create();
        keys.stream().
            filter( NodeKey::isPath ).
            map( NodeKey::getAsPath ).
            forEach( nodePaths::addNodePath );
        nodeService.getByPaths( nodePaths.build() ).
            stream().
            map( Node::id ).
            forEach( nodeIds::add );
        return nodeIds.build();
    }

    public static final class Builder
        extends AbstractNodeHandler.Builder<Builder>
    {
        private NodeKeys keys;

        private String message;

        private Builder()
        {
        }

        public Builder keys( final NodeKeys val )
        {
            keys = val;
            return this;
        }

        public Builder message( final String val )
        {
            message = val;
            return this;
        }

        public CommitNodeHandler build()
        {
            return new CommitNodeHandler( this );
        }
    }
}
